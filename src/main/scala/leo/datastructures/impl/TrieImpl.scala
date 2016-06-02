package leo.datastructures.impl

import leo.datastructures.Trie

/**
  * Created by lex on 28.02.16.
  */
protected[datastructures] abstract class HashMapTrieImpl[K,V] extends leo.datastructures.Trie[K,V] {
  import scala.collection.immutable.HashMap

  protected[this] var subTrieMap: Map[K, HashMapTrieImpl[K,V]] = HashMap[K, HashMapTrieImpl[K,V]]()

  protected[this] def apply(keyLength: Int): HashMapTrieImpl[K,V]
  protected[this] def addValue(entry: V): Unit

  def insert(key: Seq[K], entry: V): Unit = {
    if (key.isEmpty) {
      addValue(entry)
    } else {
      val (hd,tail) = (key.head, key.tail)
      if (subTrieMap.contains(hd)) {
        subTrieMap(hd).insert(tail, entry)
      } else {
        val newSubTrie = apply(tail.length)
        newSubTrie.insert(tail, entry)
        subTrieMap = subTrieMap + (hd -> newSubTrie)
      }
    }
  }

  def get(key: Seq[K]): Set[V] = subTrie(key).fold(Set[V]())(_.valueSet)

  def getAll: Set[V] = valueSet ++ subTrieMap.values.flatMap(_.getAll)

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

  def isLeaf: Boolean = subTrieMap.isEmpty
}

// #################################

protected[datastructures] class DefaultHashMapTrieImpl[K,V] extends HashMapTrieImpl[K,V] {
  import scala.collection.immutable.HashSet
  protected[this] var values: Set[V] = HashSet[V]()

  protected[this] def apply(keyLength: Int): HashMapTrieImpl[K, V] = new DefaultHashMapTrieImpl[K,V]
  protected[this] def addValue(entry: V): Unit = {
    values = values + entry
  }
  def valueSet: Set[V] = values
}

// #################################


protected[datastructures] class FixedLengthHashMapTrieImpl[K,V] extends HashMapTrieImpl[K,V] with leo.datastructures.FixedLengthTrie[K,V] {
  protected[this] def apply(keyLength: Int): HashMapTrieImpl[K, V] = {
    if (keyLength == 0) Leaf
    else new FixedLengthHashMapTrieImpl[K,V]
  }

  protected[this] def addValue(entry: V): Unit = throw new IllegalArgumentException
  def valueSet: Set[V] = Set.empty



  private object Leaf extends FixedLengthHashMapTrieImpl[K,V] {
    import scala.collection.immutable.HashSet
    protected[this] var values: Set[V] = HashSet[V]()
//    override var subTrieMap = null

    override protected[this] def apply(keyLength: Int): HashMapTrieImpl[K, V] = throw new IllegalArgumentException
    override protected[this] def addValue(entry: V): Unit = {values = values + entry}


    override def insert(key: Seq[K], entry: V): Unit = {
      if (key.isEmpty) addValue(entry)
      else throw new IllegalArgumentException
    }

    override def valueSet: Set[V] = values

    override def get(key: Seq[K]): Set[V] = getPrefix(key)
    override def getAll: Set[V] = values
    override def subTrie(prefix: Seq[K]): Option[Trie[K, V]] = {
     if (prefix.isEmpty) Some(this)
     else None
    }
    override def getPrefix(prefix: Seq[K]): Set[V] = {
      if (prefix.isEmpty) values
      else Set.empty
    }
    override def iterator: Iterator[Trie[K, V]] =  Iterator.empty
    override def isEmpty: Boolean = values.isEmpty
  }
}
