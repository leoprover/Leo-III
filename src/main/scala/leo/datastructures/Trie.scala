package leo.datastructures

/**
  * Trait for mutable tries of sets of object of some type in the leafs and sequences of some data on the paths.
  *
  * @author Alexander Steen <a.steen@fu-berlin.de>
  */
trait Trie[Key, Value] {
  /** Insert an element `entry` unter path described by `key`. */
  def insert(key: Seq[Key],  entry: Value): Unit
  /** Get the set of values that is saved at position `key`. */
  def get(key: Seq[Key]): Set[Value]
  /** Get the set of values that is saved at positions that have prefix `prefix`. */
  def getPrefix(prefix: Seq[Key]): Set[Value]
  /** Get all elements that are saved in the trie. */
  def getAll: Set[Value]
  /** Get all elements that are saved at the root position of this trie. */
  def valueSet: Set[Value]
  /** Returns `true` iff there is no element at any position. */
  def isEmpty: Boolean

  /** Iterator over the subtries that begin at root position. */
  def iterator: Iterator[Trie[Key, Value]]

  /** Returns the subtrie that is located at position `prefix` */
  def subTrie(prefix: Seq[Key]): Trie[Key, Value]
}

/**
  * Special-case of `Trie` where each entry key has the same length.
  *
  * @author Alexander Steen <a.steen@fu-berlin.de>
  */
trait FixedLengthTrie[Key, Value] extends Trie[Key, Value]

object Trie {
  import impl.HashMapTrieImpl
  def apply[K,V](): Trie[K,V] = new HashMapTrieImpl[K,V]()
  def apply[K,V](key: Seq[K], entry: V): Trie[K,V] = {
    val newTrie = apply[K,V]()
    newTrie.insert(key, entry)
    newTrie
  }
}