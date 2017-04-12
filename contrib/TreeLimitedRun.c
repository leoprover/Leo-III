//---------------------------------------------------------------------------
//----Program to watch CPU usage of a process
//----TreeLimitedRun <CPU time limit> <WC time limit> <Memory limit> <Job>
//---------------------------------------------------------------------------
//----SUN or LINUX or OSX (OSX is very generic)
#if (!defined(SUN) && !defined(LINUX) && !defined(OSX))
    #define LINUX
#endif
//---------------------------------------------------------------------------
#include <stdio.h>
#include <string.h>
#include <sys/time.h>
#include <sys/resource.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <stdlib.h>
#include <unistd.h>
#include <dirent.h>
#include <ctype.h>
#include <signal.h>
#include <errno.h>
#ifdef SUN
#include <procfs.h>
#endif
//---------------------------------------------------------------------------
#define STRING_LENGTH 80
#define MAX_PROCESSES 1000
#define DEFAULT_DELAY_BETWEEN_CHECKS 10
#define NANOSECONDS 1E9
#define MICROSECONDS 1E6
#define JIFFIES 100

#define STDOUT 1
#define STDERR 2

typedef char String[STRING_LENGTH];

typedef struct {
    int Active;
    pid_t PID;
    pid_t PPID;
    double CPUTime;
    double AccumulatedCPUTime;
} ProcessData;

typedef ProcessData ProcessDataArray[MAX_PROCESSES];

int GlobalInterrupted;
int GlobalSignalReceived;
//---------------------------------------------------------------------------
void SIGCHLDHandler(int TheSignal) {

//DEBUG printf("Some child has died\n");
//----The child is reaped in WatchChildTree

}
//---------------------------------------------------------------------------
void SIGCHLDReaper(int TheSignal) {

    int DeadPID;
    int Status;

    while ((DeadPID = waitpid(-1,&Status,WNOHANG)) > 0) {
//DEBUG printf("!!! Child %d of TreeLimitedRun %d has died\n",DeadPID,getpid());fflush(stdout);
    }

}
//---------------------------------------------------------------------------
//----Controllers in the CASC/SystemOnTPTP/SSCPA/etc hierarchy may send
//----SIGQUIT to stop things
void SIGQUITHandler(int TheSignal) {

//DEBUG printf("!!! TreeLimitedRun %d got a signal %d\n",getpid(),TheSignal);fflush(stdout);
    GlobalInterrupted = 1;
    GlobalSignalReceived = TheSignal;

}
//---------------------------------------------------------------------------
void SetCPUTimeLimit(rlim_t CPUTimeLimit) {

    struct rlimit ResourceLimits;

//----Set the signal handler
//----No point - signals are reset on exec
//----For some reason this is still needed, or the signal doesn't work
    if (signal(SIGXCPU,SIGQUITHandler) == SIG_ERR) {
        perror("Setting signal handler");
        exit(EXIT_FAILURE);
    }

//----Limit the CPU time. Need to get old one for hard limit field
    if (getrlimit(RLIMIT_CPU,&ResourceLimits) == -1) {
        perror("Getting CPU limit");
        exit(EXIT_FAILURE);
    }
//----Set new CPU limit in ms (sent in secs)
    ResourceLimits.rlim_cur = CPUTimeLimit;
    if (setrlimit(RLIMIT_CPU,&ResourceLimits) == -1) {
        perror("Setting CPU limit");
        exit(EXIT_FAILURE);
    }
}
//-----------------------------------------------------------------------------
void SetMemoryLimit(rlim_t MemoryLimit) {

    struct rlimit ResourceLimits;

#ifdef RLIMIT_AS
#define THE_LIMIT RLIMIT_AS
#else
#define THE_LIMIT RLIMIT_DATA
#endif

//----Limit the memory. Need to get old one for hard limit field
    if (getrlimit(THE_LIMIT,&ResourceLimits) == -1) {
        perror("Getting memory limit");
        exit(EXIT_FAILURE);
    }
//----Set new memory limit
    ResourceLimits.rlim_max = MemoryLimit;
    ResourceLimits.rlim_cur = MemoryLimit;
    if (setrlimit(THE_LIMIT,&ResourceLimits) == -1) {
        perror("Setting memory limit");
        exit(EXIT_FAILURE);
    }
}
//---------------------------------------------------------------------------
//----Prevent core dumps that occur on timeout
void SetNoCoreDump(void) {

    struct rlimit ResourceLimits;

//----Get old resource limits
    if (getrlimit(RLIMIT_CORE,&ResourceLimits) == -1) {
        perror("Getting resource limit:");
        exit(EXIT_FAILURE);
    }
//----Set new core limit to 0
    ResourceLimits.rlim_cur = 0;
    if (setrlimit(RLIMIT_CORE,&ResourceLimits) == -1) {
        perror("Setting resource limit:");
        exit(EXIT_FAILURE);
    }
}
//---------------------------------------------------------------------------
#ifdef LINUX
void GetProcessesOwnedByMe(uid_t MyRealUID,ProcessDataArray OwnedPIDs,
int *NumberOfOwnedPIDs) {

    DIR *ProcDir;
    struct dirent *ProcessDir;
    pid_t UID,PID,PPID;
    FILE *ProcFile;
    String ProcFileName,Line;

    if ((ProcDir = opendir("/proc")) == NULL) {
        perror("ERROR: Cannot opendir /proc\n");
        exit(EXIT_FAILURE);
    }
//DEBUG printf("look for processes owned by %d\n",MyRealUID);

    *NumberOfOwnedPIDs = 0;
    while ((ProcessDir = readdir(ProcDir)) != NULL) {
        if (isdigit(ProcessDir->d_name[0])) {
            PID = (pid_t)atoi(ProcessDir->d_name);
            sprintf(ProcFileName,"/proc/%d/status",PID);
            if ((ProcFile = fopen(ProcFileName,"r")) != NULL) {
                PPID = -1;
                UID = -1;
                while ((PPID == -1 || UID == -1) &&
fgets(Line,STRING_LENGTH,ProcFile) != NULL) {
                    sscanf(Line,"PPid: %d",&PPID);
                    sscanf(Line,"Uid: %d",&UID);
                }
                fclose(ProcFile);
//----Check that data was found
//DEBUG printf("PID = %d PPID = %d UID = %d\n",PID,PPID,UID);
                if (PPID == -1 || UID == -1) {
                    fprintf(stderr,"Could not get process information\n");
                    exit(EXIT_FAILURE);
                }
//----Check if this process is owned by this user
                if (UID == MyRealUID) {
//----Record the PIDs as potentially relevant
                    OwnedPIDs[*NumberOfOwnedPIDs].Active = 1;
                    OwnedPIDs[*NumberOfOwnedPIDs].PID = PID;
                    OwnedPIDs[*NumberOfOwnedPIDs].PPID = PPID;
                    (*NumberOfOwnedPIDs)++;
//DEBUG printf("%d I own PID = %d PPID = %d UID = %d\n",*NumberOfOwnedPIDs,PID,PPID,UID);
                    if (*NumberOfOwnedPIDs >= MAX_PROCESSES) {
                        fprintf(stderr,"ERROR: Out of save process space\n");
                        exit(EXIT_FAILURE);
                    }
                }
            } else {
//----Bloody child just died
            }
        }
    }
    closedir(ProcDir);
}
//---------------------------------------------------------------------------
float GetProcessTime(pid_t PID,int IncludeSelf,int IncludeChildren) {

    FILE *ProcFile;
    String ProcFileName;
    float MyTime,ChildTime,ProcessTime;
    int UserModeJiffies,SystemModeJiffies,ChildUserModeJiffies,
ChildSystemModeJiffies;

    ProcessTime = 0;
    sprintf(ProcFileName,"/proc/%d/stat",PID);
    if ((ProcFile = fopen(ProcFileName,"r")) != NULL) {
        fscanf(ProcFile,"%*d %*s %*c %*d %*d %*d %*d %*d %*u %*u %*u %*u %*u %d %d %d %d",&UserModeJiffies,&SystemModeJiffies,&ChildUserModeJiffies,
&ChildSystemModeJiffies);
        fclose(ProcFile);
//DEBUG printf("%d: my jiffies = %d, dead child jiffies = %d\n",PID,UserModeJiffies+SystemModeJiffies,ChildUserModeJiffies+ChildSystemModeJiffies);
//----Time used by this process
        MyTime = ((float)(UserModeJiffies + SystemModeJiffies))/JIFFIES;
//----Time used by this process's dead children (man pages are wrong - does
//----not include my jiffies)
        ChildTime = ((float)(ChildUserModeJiffies + ChildSystemModeJiffies))/
JIFFIES;
        if (IncludeSelf) {
            ProcessTime += MyTime;
        }
        if (IncludeChildren) {
            ProcessTime += ChildTime;
        }
//DEBUG printf("Process time for %d is %f\n",PID,ProcessTime);
        return(ProcessTime);
    } else {
//----Bloody process died, return 0 and catch it in the parent next time
        return(0);
    }
}
#endif
//---------------------------------------------------------------------------
#ifdef SUN
void GetProcessesOwnedByMe(uid_t MyRealUID,ProcessDataArray OwnedPIDs,
int *NumberOfOwnedPIDs) {

    DIR *ProcDir;
    struct dirent *ProcessDir;
    pid_t PID;
    struct psinfo ProcessRecord;
    FILE *ProcFile;
    String ProcFileName;

    if ((ProcDir = opendir("/proc")) == NULL) {
        perror("ERROR: Cannot opendir /proc\n");
        exit(EXIT_FAILURE);
    }

//DEBUG printf("look for processes owned by %d\n",MyRealUID);

    *NumberOfOwnedPIDs = 0;
    while ((ProcessDir = readdir(ProcDir)) != NULL) {
        if (isdigit((int)ProcessDir->d_name[0])) {
            PID = (pid_t)atoi(ProcessDir->d_name);
            sprintf(ProcFileName,"/proc/%d/psinfo",(int)PID);
            if ((ProcFile = fopen(ProcFileName,"r")) != NULL) {
                fread(&ProcessRecord,sizeof(ProcessRecord),1,ProcFile);
                fclose(ProcFile);
//----Check if this process is owned by this user
                if (ProcessRecord.pr_uid == MyRealUID) {
//----Record the PIDs as potentially relevant
                    OwnedPIDs[*NumberOfOwnedPIDs].PID = PID;
                    OwnedPIDs[*NumberOfOwnedPIDs].PPID = ProcessRecord.pr_ppid;
                    (*NumberOfOwnedPIDs)++;
                    if (*NumberOfOwnedPIDs >= MAX_PROCESSES) {
                        fprintf(stderr,"ERROR: Out of save process space\n");
                        exit(EXIT_FAILURE);
                    }
                }
            } else {
//----Bloody child just died
            }
        }
    }
    closedir(ProcDir);
}
//---------------------------------------------------------------------------
float GetProcessTime(pid_t PID,int IncludeSelf,int IncludeChildren) {

    FILE *ProcFile;
    String ProcFileName;
    pstatus_t StatusRecord;
    float ProcessTime;

    ProcessTime = 0;
    sprintf(ProcFileName,"/proc/%d/status",(int)PID);
    if ((ProcFile = fopen(ProcFileName,"r")) != NULL) {
        fread(&StatusRecord,sizeof(StatusRecord),1,ProcFile);
        fclose(ProcFile);
        if (IncludeSelf) {
            ProcessTime += StatusRecord.pr_utime.tv_sec +
StatusRecord.pr_stime.tv_sec +
((float)(StatusRecord.pr_utime.tv_nsec+StatusRecord.pr_stime.tv_nsec))/
NANOSECONDS;
        }
        if (IncludeChildren) {
            ProcessTime += StatusRecord.pr_cutime.tv_sec +
StatusRecord.pr_cstime.tv_sec +
((float)(StatusRecord.pr_cutime.tv_nsec+StatusRecord.pr_cstime.tv_nsec))/
NANOSECONDS;
        }
//DEBUG printf("Process %d has used U %ld +n%ld + S %ld +n%ld + CU %ld +n%ld + CS %ld +n%ld = %.1f\n",
//DEBUG PID,StatusRecord.pr_utime.tv_sec,StatusRecord.pr_utime.tv_nsec,
//DEBUG StatusRecord.pr_stime.tv_sec,StatusRecord.pr_stime.tv_nsec,
//DEBUG StatusRecord.pr_cutime.tv_sec,StatusRecord.pr_cutime.tv_nsec,
//DEBUG StatusRecord.pr_cstime.tv_sec,StatusRecord.pr_cstime.tv_nsec,
//DEBUG ProcessTime);
        return(ProcessTime);
    } else {
//----Bloody child died, return 0 and catch it in the parent next time
        return(0);
    }
}
#endif //----SUN
//---------------------------------------------------------------------------
#ifdef OSX
//----No looking in /proc for OSX
#endif
//---------------------------------------------------------------------------
int PIDInArray(pid_t PID,ProcessDataArray PIDs,int NumberOfPIDs) {

    int PIDIndex;

    for (PIDIndex = 0;PIDIndex < NumberOfPIDs;PIDIndex++) {
        if (PIDs[PIDIndex].PID == PID) {
            return(1);
        }
    }
    return(0);
}
//---------------------------------------------------------------------------
#if (defined(LINUX)||defined(SUN))
//----Only for systems that have /proc

int GetTreeTimes(uid_t MyRealUID,pid_t FirstBornPID,ProcessDataArray 
TreeTimes) {

    ProcessDataArray OwnedPIDs;
    int NumberOfOwnedPIDs;
    int NumberOfTreeTimes;
    int CurrentTreeIndex;
    int OwnedIndex;

//----Get the list of processes owned by this user
    GetProcessesOwnedByMe(MyRealUID,OwnedPIDs,&NumberOfOwnedPIDs);

//----Check that the root of the tree is still there
//DEBUG printf("Check if %d is alive\n",FirstBornPID);
    if (!PIDInArray(FirstBornPID,OwnedPIDs,NumberOfOwnedPIDs)) {
//DEBUG printf("It is dead\n");
        return(0);
    }
//DEBUG printf("It is alive\n");

//----Find those in the process tree, and get their times
    CurrentTreeIndex = 0;
    TreeTimes[0].Active = 1;
    TreeTimes[0].PID = FirstBornPID;
    TreeTimes[0].PPID = MyRealUID;
    TreeTimes[0].CPUTime = GetProcessTime(TreeTimes[0].PID,1,1);
    NumberOfTreeTimes = 1;

    while (CurrentTreeIndex < NumberOfTreeTimes) {
//DEBUG printf("%d %d is in the tree\n",TreeTimes[CurrentTreeIndex].PID,TreeTimes[CurrentTreeIndex].PPID);
//----Scan for offspring
        TreeTimes[CurrentTreeIndex].CPUTime = 
GetProcessTime(TreeTimes[CurrentTreeIndex].PID,1,1);
        for (OwnedIndex = 0; OwnedIndex < NumberOfOwnedPIDs; OwnedIndex++) {
            if (OwnedPIDs[OwnedIndex].PPID == TreeTimes[CurrentTreeIndex].PID) {
                TreeTimes[NumberOfTreeTimes].Active = 1;
                TreeTimes[NumberOfTreeTimes].PID = OwnedPIDs[OwnedIndex].PID;
                TreeTimes[NumberOfTreeTimes].PPID = OwnedPIDs[OwnedIndex].PPID;
                NumberOfTreeTimes++;
            }
        }
//----Move on to the next process in the tree
        CurrentTreeIndex++;
    }

    return(NumberOfTreeTimes);
}

#endif
#ifdef OSX
//----No /proc for OSX
#endif
//---------------------------------------------------------------------------
//----Send signals and reports if process is known to be gone
int SignalAndReport(int PID,int Signal,int RepeatUntilTerminated,
char * ProcessType) {

    String ErrorMessage;
    extern int errno;
    int Terminated;
    int NumberOfLoops;

    Terminated = 0;
    NumberOfLoops = 0;
    do {
        NumberOfLoops++;
//DEBUG printf("!!! TreeLimitedRun %d sends signal %d to %s %d\n",getpid(),Signal,ProcessType,PID);fflush(stdout);
        if (kill(PID,Signal) != 0) {
//DEBUG printf("The kill errno is %d\n",errno);
//----If process no longer exists record that to avoid killing again
            if (errno == ESRCH) {
                Terminated = 1;
            } else {
                sprintf(ErrorMessage,
"!!! ERROR: TreeLimitedRun %d cannot signal %s %d with %d",getpid(),ProcessType,
PID,Signal);
                perror(ErrorMessage);
            }
        }
//----Must do perror after errno, as perror clears errno
    } while (RepeatUntilTerminated && !Terminated && NumberOfLoops < 10);

    return(Terminated);
}
//---------------------------------------------------------------------------
void ChildKillTree(int TargetIndex,ProcessDataArray TreeTimes,
int NumberInTree,int Signal) {

    int ChildIndex;

//DEBUG printf("!!! TreeLimitedRun %d killing tree below PID %d with %d\n",getpid(),TreeTimes[TargetIndex].PID,Signal);fflush(stdout);
    for (ChildIndex=0; ChildIndex < NumberInTree; ChildIndex++) {
        if (TreeTimes[TargetIndex].PID == TreeTimes[ChildIndex].PPID) {
            ChildKillTree(ChildIndex,TreeTimes,NumberInTree,Signal);
        }
//TOO SLOW? usleep(100000);
    }
    if (TreeTimes[TargetIndex].Active) {
        if (SignalAndReport(TreeTimes[TargetIndex].PID,Signal,
Signal == SIGKILL,"tree process")) {
            TreeTimes[TargetIndex].Active = 0;
        }
    }
}
//---------------------------------------------------------------------------
int KillTree(uid_t MyRealUID,pid_t FirstBornPID,int Signal) {

    int NumberOfTreeTimes;
    extern int errno;

#if (defined(LINUX)||defined(SUN))
    ProcessDataArray TreeTimes;

    if ((NumberOfTreeTimes = GetTreeTimes(MyRealUID,FirstBornPID,TreeTimes)) > 
0) {

//----The first born gets it first so that it can curb its descendants nicely
        if (TreeTimes[0].Active) {
//DEBUG printf("!!! TreeLimitedRun %d killing top process %d with %d\n",getpid(),TreeTimes[0].PID,Signal);fflush(stdout);
//----TreeTimes[0].PID is FirstBornPID
            if (SignalAndReport(TreeTimes[0].PID,Signal,Signal == SIGKILL,
"top process")) {
                TreeTimes[0].Active = 0;
            }
        }

//----200000 is not enough - EP's eproof script gets killed before it can
//----kill eprover
        usleep(500000);
//----Now gently from the bottom up
        ChildKillTree(0,TreeTimes,NumberOfTreeTimes,Signal);
        usleep(100000);
//----Now viciously from the bottom up
        ChildKillTree(0,TreeTimes,NumberOfTreeTimes,SIGKILL);
    }
#endif
#ifdef OSX
    int Active;

    Active = 1;
//----The first born gets it first so that it can curb its descendants nicely
    if (SignalAndReport(FirstBornPID,Signal,Signal == SIGKILL,"top process")) {
        Active = 0;
    }
    if (Active) {
        usleep(100000);
//----Now viciously
        SignalAndReport(FirstBornPID,SIGKILL,1,"only process");
    }
    NumberOfTreeTimes = 1;
#endif

    return(NumberOfTreeTimes);
}
//---------------------------------------------------------------------------
void KillOrphans(uid_t MyRealUID,ProcessDataArray SavePIDs,
int NumberOfSavePIDs) {

#if (defined(LINUX)||defined(SUN))

    ProcessDataArray OwnedPIDs;
    int NumberOfOwnedPIDs;
    int OwnedIndex;
    int NumberOfOrphansKilled;
    extern int errno;

    do {
//----Get the list of processes owned by this user
        GetProcessesOwnedByMe(MyRealUID,OwnedPIDs,&NumberOfOwnedPIDs);

        NumberOfOrphansKilled = 0;
        for (OwnedIndex = 0; OwnedIndex < NumberOfOwnedPIDs; OwnedIndex++) {
//DEBUG printf("!!! TreeLimitedRun %d considers %d with parent %d\n",getpid(),OwnedPIDs[OwnedIndex].PID, OwnedPIDs[OwnedIndex].PPID);fflush(stdout);
            if (OwnedPIDs[OwnedIndex].PPID == 1 &&
!PIDInArray(OwnedPIDs[OwnedIndex].PID,SavePIDs,NumberOfSavePIDs)) {
//DEBUG printf("!!! TreeLimitedRun %d kills orphan %d\n",getpid(),OwnedPIDs[OwnedIndex].PID);fflush(stdout);
                if (SignalAndReport(OwnedPIDs[OwnedIndex].PID,SIGQUIT,0,
"orphan")) {
                    OwnedPIDs[OwnedIndex].Active = 0;
                }
                if (OwnedPIDs[OwnedIndex].Active) {
                    sleep(1);
                    SignalAndReport(OwnedPIDs[OwnedIndex].PID,SIGKILL,1,
"orphan");
                }
                NumberOfOrphansKilled++;
            }
        }
        if (NumberOfOrphansKilled > 0) {
            printf("Killed %d orphans\n",NumberOfOrphansKilled);
        }
    } while (NumberOfOrphansKilled > 0);

#endif
#ifdef OSX
//----No orphans known for OSX
#endif
}
//---------------------------------------------------------------------------
void PrintTimes(char* Tag,float TreeCPUTime,float WCTime) {

//----You can print times with more accuracy here
    printf("%s: %.1f CPU %.1f WC\n",Tag,TreeCPUTime,WCTime);
    fflush(NULL);

}
//---------------------------------------------------------------------------
float WallClockSoFar(struct timeval WCStartTime) {

    struct timeval WCEndTime;

    gettimeofday(&WCEndTime,NULL);
//DEBUG printf("Started at %ld +%f and ended at %ld +%f\n",
//DEBUG WCStartTime.tv_sec,WCStartTime.tv_usec/MICROSECONDS,
//DEBUG WCEndTime.tv_sec,WCEndTime.tv_usec/MICROSECONDS);

    return((WCEndTime.tv_sec - WCStartTime.tv_sec) +
(WCEndTime.tv_usec - WCStartTime.tv_usec)/MICROSECONDS);

}
//---------------------------------------------------------------------------
double AccumulateTreeTime(int TargetIndex,ProcessDataArray TreeTimes,
int NumberInTree) {

    int ChildIndex;

    TreeTimes[TargetIndex].AccumulatedCPUTime = TreeTimes[TargetIndex].CPUTime;
    for (ChildIndex=0; ChildIndex < NumberInTree; ChildIndex++) {
        if (TreeTimes[TargetIndex].PID == TreeTimes[ChildIndex].PPID) {
            TreeTimes[TargetIndex].AccumulatedCPUTime += 
AccumulateTreeTime(ChildIndex,TreeTimes,NumberInTree);
        }
    }

    return(TreeTimes[TargetIndex].AccumulatedCPUTime);
}
//---------------------------------------------------------------------------
float WatchChildTree(int MyPID,int ChildPID,int CPUTimeLimit,
int DelayBetweenChecks,struct timeval WCStartTime,int PrintEachCheck) {

    double TreeTime,LastTreeTime,LostTime;
    int NumberInTree;
    int KilledInTree;
    int Status;
    int DeadPID;
#if (defined(LINUX)||defined(SUN))
    ProcessDataArray TreeTimes;
#endif
#ifdef OSX
    struct rusage ResourceUsage;
#endif

    LastTreeTime = 0.0;
    LostTime = 0.0;

//----Loop watching times taken. Order is important - get time before
//----checking for interrupt
    do {
#if (defined(LINUX)||defined(SUN))
//----Look at the tree
        NumberInTree = GetTreeTimes(getuid(),ChildPID,TreeTimes);
        TreeTime = AccumulateTreeTime(0,TreeTimes,NumberInTree);
//DEBUG fprintf(stderr,"now %5.2f limit %d\n",TreeTime,CPUTimeLimit);
//----For those with /proc, reap the children. Need to reap late so /proc
//----entries do not disappear
        while ((DeadPID = waitpid(-1,&Status,WNOHANG)) > 0) {
            NumberInTree--;
//DEBUG fprintf(stderr,"The child %d has died\n",DeadPID);
        }
#endif
#ifdef OSX
//----Check if child is gone (-1 if no child, 0 if not dead, PID if dead)
        DeadPID = waitpid(-1,&Status,WNOHANG);
        if (DeadPID == ChildPID || DeadPID == -1) {
            TreeTime = LastTreeTime;
            NumberInTree = 0;
        } else {
            if (getrusage(RUSAGE_CHILDREN,&ResourceUsage) != -1) {
                TreeTime = ResourceUsage.ru_utime.tv_sec +
ResourceUsage.ru_utime.tv_usec / 1000000 + ResourceUsage.ru_stime.tv_sec +
ResourceUsage.ru_stime.tv_usec / 1000000;
            } else {
                printf("TreeLimitedRun could not getrusage\n");
                TreeTime = LastTreeTime;
            }
//----Maybe more than 1, but we don't know in OSX version
            NumberInTree = 1;
        }
#endif

//DEBUG fprintf(stderr,"acc time is %.2f\n",TreeTime);
        if (TreeTime < LastTreeTime) {
            LostTime += LastTreeTime - TreeTime;
            printf("WARNING: TreeLimitedRun lost %.2fs, total lost is %.2fs\n",
LastTreeTime - TreeTime,LostTime);
        }
//DEBUG printf("lost time is %.2f\n",LostTime);
        LastTreeTime = TreeTime;
        TreeTime += LostTime;

//----Print each loop if requested
        if (PrintEachCheck) {
            PrintTimes("WATCH",TreeTime,WallClockSoFar(WCStartTime));
        }
//----If we're going to loop, wait a bit first
//----DANGER - if the last descedantprocess dies between GetTreeTimes() and 
//----here, it still waits.
        if ((CPUTimeLimit == 0 || TreeTime <= CPUTimeLimit) &&
NumberInTree > 0 && !GlobalInterrupted) {
            sleep(DelayBetweenChecks);
        }
    } while ((CPUTimeLimit == 0 || TreeTime <= CPUTimeLimit) && 
NumberInTree > 0 && !GlobalInterrupted);

//----From now on reap normally
    if (signal(SIGCHLD,SIGCHLDReaper) == SIG_ERR) {
        perror("ERROR: Could not set SIGCHLD handler");
        exit(EXIT_FAILURE);
    }
//----Reap anyway in case I missed some
    SIGCHLDReaper(0);

//----If over time limit, stop them all (XCPU to top guy first)
    if (NumberInTree > 0 && TreeTime > CPUTimeLimit) {
        KilledInTree = KillTree(getuid(),ChildPID,SIGXCPU);
//DEBUG printf("Killed %d in tree\n",KilledInTree);
    }

//----If global interrupted, then send it on
    if (NumberInTree > 0 && GlobalInterrupted) {
        KilledInTree = KillTree(getuid(),ChildPID,GlobalSignalReceived);
//DEBUG printf("Killed %d in tree\n",KilledInTree);
    }

    return(TreeTime);
}
//---------------------------------------------------------------------------
int main(int argc,char *argv[]) {

    int CPUTimeLimit;
    int WCTimeLimit;
    int MemoryLimit;
    int ArgNumber;
    int QuietnessLevel;
    int ArgOffset;
    pid_t ChildPID;
    float TreeCPUTime;
    float WCTime;
    struct timeval WCStartTime;
    int DelayBetweenChecks;
    int PrintEachCheck = 0;
    ProcessDataArray SavePIDs;
    int NumberOfSavePIDs;
    int Status;

//----Check the quietness level
    if (argc >= 2 && strstr(argv[1],"-q") == argv[1]) {
        ArgOffset = 1;
        QuietnessLevel = atoi(&argv[ArgOffset][2]);
    } else {
        QuietnessLevel = 1;
        ArgOffset = 0;
    }

//----Look for time and print flags
    if (argc >= ArgOffset+2 &&
strstr(argv[ArgOffset+1],"-t") == argv[ArgOffset+1]) {
        ArgOffset++;
        DelayBetweenChecks = atoi(&argv[ArgOffset][2]);
    } else {
        if (argc >= ArgOffset+2 &&
strstr(argv[ArgOffset+1],"-p") == argv[ArgOffset+1]) {
            PrintEachCheck = 1;
            ArgOffset++;
            DelayBetweenChecks = atoi(&argv[ArgOffset][2]);
        } else {
            DelayBetweenChecks = DEFAULT_DELAY_BETWEEN_CHECKS;
        }
    }

    if (argc - ArgOffset >= 4) {
//----Redirect stderr to stdout
        if (dup2(STDOUT,STDERR) == -1) {
            perror("ERROR: Cannot dup STDERR to STDOUT");
        }

//----Extract time limits
        CPUTimeLimit = atoi(argv[ArgOffset+1]);
        WCTimeLimit = atoi(argv[ArgOffset+2]);
        if (isdigit((int)argv[ArgOffset+3][0])) {
            MemoryLimit = atoi(argv[ArgOffset+3]);
            ArgOffset++;
        } else {
            MemoryLimit = 0;
        }

        if (QuietnessLevel == 0) {
            printf(
"TreeLimitedRun: ----------------------------------------------------------\n");
            printf("TreeLimitedRun: %s ",argv[ArgOffset+3]);
            for (ArgNumber=ArgOffset+4;ArgNumber<argc;ArgNumber++)
                printf("%s ",argv[ArgNumber]);
            printf("\n");
            printf("TreeLimitedRun: CPU time limit is %ds\n",CPUTimeLimit);
            printf("TreeLimitedRun: WC  time limit is %ds\n",WCTimeLimit);
            if (MemoryLimit > 0) {
                printf("TreeLimitedRun: Memory   limit is %dbytes\n",
MemoryLimit);
            }
//----Output the PID for possible later use
            printf("TreeLimitedRun: PID is %d\n",(int)getpid());
            printf(
"TreeLimitedRun: ----------------------------------------------------------\n");
            fflush(stdout);
        }
        SetNoCoreDump();

//----Set handler for when child dies
        if (signal(SIGCHLD,SIGCHLDHandler) == SIG_ERR) {
            perror("ERROR: Could not set SIGCHLD handler");
            exit(EXIT_FAILURE);
        }
//----Set handler for global interruptions and alarms
        if (signal(SIGQUIT,SIGQUITHandler) == SIG_ERR) {
            perror("ERROR: Could not set SIGQUIT handler");
            exit(EXIT_FAILURE);
        }
        if (signal(SIGALRM,SIGQUITHandler) == SIG_ERR) {
            perror("ERROR: Could not set SIGALRM handler");
            exit(EXIT_FAILURE);
        }

#if (defined(LINUX)||defined(SUN))
//----Record running processes at start (xeyes, gnome, etc)
        GetProcessesOwnedByMe(getuid(),SavePIDs,&NumberOfSavePIDs);
#endif
#ifdef OSX
//----No recording processes for OSX
#endif

//----Fork for ATP process
        if ((ChildPID = fork()) == -1) {
            perror("ERROR: Cannot fork for ATP system process");
            exit(EXIT_FAILURE);
        }

//----In child, set limits and execute the ATP system
        if (ChildPID == 0) {
            if (setvbuf(stdout,NULL,_IONBF,0) != 0) {
                perror("Setting unbuffered");
            }
//DEBUG printf("The prover PID will be %d\n",getpid());
//----Set memory limit for child only
            if (MemoryLimit > 0) {
                SetMemoryLimit(MemoryLimit);
            }

#if (defined(LINUX)||defined(SUN))
//----Systems with /proc are stopped by this program with kill
#endif
#ifdef OSX
//----In OSX case, child must limit itself
            if (CPUTimeLimit > 0) {
                SetCPUTimeLimit(CPUTimeLimit);
            }
#endif

            execvp(argv[ArgOffset+3],argv+ArgOffset+3);
            perror("Cannot exec");
            exit(EXIT_FAILURE);

//----In parent, set limits and watch the ATP system
        } else {
            if (WCTimeLimit > 0) {
                alarm(WCTimeLimit);
            }
//----Record start time
            gettimeofday(&WCStartTime,NULL);
            TreeCPUTime = 0;
            WCTime = 0;

//----Set global for interrupt handler
            GlobalInterrupted = 0;
            GlobalSignalReceived = 0;

//----Watch the tree of processes
            TreeCPUTime = WatchChildTree(getpid(),ChildPID,CPUTimeLimit,
DelayBetweenChecks,WCStartTime,PrintEachCheck);

//----Record end WC time
            WCTime = WallClockSoFar(WCStartTime);

//----See if the time is increased by looking at my children
//----Not sure what this was for
//            ChildTime = GetProcessTime(getpid(),0,1);
//            if (ChildTime > TreeCPUTime) {
//                TreeCPUTime = ChildTime;
//            }

            PrintTimes("FINAL WATCH",TreeCPUTime,WCTime);

//----Sweep for orphans
            KillOrphans(getuid(),SavePIDs,NumberOfSavePIDs);
//----Reap any remaining
            while (wait(&Status) != -1) {
                sleep(1);
//DEBUG printf("!!! Waiting for children that havn't died\n");
            }
        }
    } else {
        printf("Usage: %s [-q<quietness>] [-t<check delay>|-p<print check delay>] <CPU limit> <WC limit> [<Memory limit>] <Job>\n",
argv[0]);
    }

    return(0);
}
//---------------------------------------------------------------------------
