package leo.datastructures.impl

import leo.datastructures.Trie

/**
  * Created by lex on 28.02.16.
  */
protected[datastructures] class HashMapTrieImpl[K,V] extends leo.datastructures.Trie[K,V] {
  import scala.collection.immutable.HashMap
  import scala.collection.immutable.HashSet

  protected[this] var subTrieMap: Map[K, HashMapTrieImpl[K,V]] = HashMap[K, HashMapTrieImpl[K,V]]()
  protected[this] var values: Set[V] = HashSet[V]()


  def insert(key: Seq[K], entry: V): Unit = {
    if (key.isEmpty) {
      this.values = this.values + entry
    } else {
      val (hd,tail) = (key.head, key.tail)
      if (subTrieMap.contains(hd)) {
        subTrieMap(hd).insert(tail, entry)
      } else {
        val newSubTrie = new HashMapTrieImpl[K,V]()
        newSubTrie.insert(tail, entry)
        subTrieMap = subTrieMap + (hd -> newSubTrie)
      }
    }
  }

  def get(key: Seq[K]): Set[V] = subTrie(key).fold(Set[V]())(_.valueSet)

  def getAll: Set[V] = valueSet ++ subTrieMap.values.flatMap(_.getAll)

  def valueSet: Set[V] = values

  def subTrie(prefix: Seq[K]): Option[Trie[K, V]] = {
    if (prefix.isEmpty) Some(this)
    else {
      val (hd,tail) = (prefix.head, prefix.tail)
      if (subTrieMap.contains(hd)) {
        subTrieMap(hd).subTrie(tail)
      } else {
        None
      }
    }
  }

  def iterator: Iterator[Trie[K, V]] = subTrieMap.valuesIterator

  def getPrefix(prefix: Seq[K]): Set[V] = subTrie(prefix).fold(Set[V]())(_.getAll)

  def isEmpty: Boolean = valueSet.isEmpty && subTrieMap.isEmpty

//  case object EmptyTrie extends leo.datastructures.Trie[K,V] {
//    def insert(key: Seq[K], entry: V): Unit = {this = new HashMapTrieImpl[K,V]}
//    def get(key: Seq[K]): Set[V] = Set.empty
//    def valueSet: Set[V] = Set.empty
//    def subTrie(prefix: Seq[K]): Trie[K, V] = EmptyTrie
//    def getPrefix(prefix: Seq[K]): Set[V] = Set.empty
//    def isEmpty: Boolean = true
//    def getAll: Set[V] = Set.empty
//    override def toString(): String = "empty"
//  }
}