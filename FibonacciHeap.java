
/**
 * FibonacciHeap
 *
 * An implementation of a Fibonacci Heap over integers.
 */
public class FibonacciHeap
{

    public HeapNode min;
    public HeapNode first;
    public int numOfNodes;
    public int marked;
    public static int links;
    public static int cuts;
    public int numOfTrees;

    public HeapNode getFirst() {
        return this.first;
    }

   /**
    * public boolean isEmpty()
    *
    * Returns true if and only if the heap is empty.
    *   
    */
    public boolean isEmpty()
    {
        return first == null;
    }
		
   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
    * The added key is assumed not to already belong to the heap.  
    * 
    * Returns the newly created node.
    */
    public HeapNode insert(int key)
    {
        HeapNode newNode = new HeapNode(key);
        if (isEmpty())
        {
            first = newNode;
            min = newNode;
        }
        else
        {
            if (min.getKey()>key)
                min = newNode;
        }
        HeapNode lastNode = first.getPrev();
        first.setPrev(newNode);
        newNode.setNext(first);
        if (lastNode != null)
        {
            newNode.setPrev(lastNode);
            lastNode.setNext(newNode);
        }
        first = newNode;
        numOfNodes += 1;
        numOfTrees += 1;
        return newNode;
    }

    /**
     * public HeapNode link(HeapNode x, HeapNode y)
     *
     * Link the nodes as the smaller node is the parent of the bigger node.
     *
     * Returns the root of the new tree.
     */

    public HeapNode link(HeapNode x, HeapNode y)
    {
        links += 1;
        numOfTrees -= 1;
        if(first == x){
            if(x.getKey() > y.getKey()){
                first = x.getNext();
            }
        }
        if(first == y){
            if(y.getKey() > x.getKey()){
                first = y.getNext();
            }
        }
        if (x.getKey() > y.getKey())
        {
            HeapNode z = y;
            y = x;
            x = z;
        }
        if (x.getChild() == null)
        {
            y.setNext(y);
            y.setPrev(y);
        }
        else
        {
            HeapNode lastChild = x.getChild().getPrev();
            lastChild.setNext(y);
            y.setPrev(lastChild);
            y.setNext(x.getChild());
            x.getChild().setPrev(y);
        }
        x.setChild(y);
        y.setParent(x);
        x.setRank(x.getRank() + 1);
        return x;
    }

   /**
    * public void deleteMin()
    *
    * Deletes the node containing the minimum key.
    *
    */
    public void deleteMin()
    {
        if (this.isEmpty())
            return;
        numOfTrees = numOfTrees - 1 + min.rank;
        numOfNodes -= 1;
        if (numOfNodes == 0)
        {
            first = null;
            min = null;
            return;
        }
        if (min == first)
            if (min.getChild() != null)
                first = min.getChild();
            else
                first = min.getNext();
        HeapNode[] arr = new HeapNode[(int) Math.ceil(Math.log(numOfNodes) / Math.log(2))+1];
        HeapNode child = min.getChild();
        HeapNode prevMin = min.getPrev();
        HeapNode nextMin = min.getNext();
        min.setChild(null);
        min.setPrev(null);
        min.setNext(null);
        if (child == null)
        {
            prevMin.setNext(nextMin);
            nextMin.setPrev(prevMin);
        }
        if (child != null) {
            child.setParent(null);
            if(child.getMarked()){
                child.setMarked(false);
                this.marked--;
            }
            HeapNode nextChild = child.getNext();
            while (nextChild != child) {
                if(nextChild.getMarked()){
                    nextChild.setMarked(false);
                    this.marked--;
                }
                nextChild.setParent(null);
                nextChild = nextChild.getNext();
            }
            if (numOfTrees == min.getRank())
            {
                first = child;
            }
            else
            {
                HeapNode lastChild = child.getPrev();
                prevMin.setNext(child);
                child.setPrev(prevMin);
                lastChild.setNext(nextMin);
                nextMin.setPrev(lastChild);
            }
        }
        HeapNode tree = first;
        HeapNode[] trees = new HeapNode[numOfTrees];
        for (int i=0; i<trees.length; i++){
            trees[i] = tree;
            tree = tree.getNext();
        }
        min = first;
        for (int i=0; i<trees.length; i++)
        {
            HeapNode node = trees[i];
            if (node.getKey() < min.getKey())
                min = node;
            int rank = node.getRank();
            while (rank<= arr.length-1 && arr[rank] != null)
            {
                HeapNode nodeInArray = arr[rank];
                HeapNode root = link(nodeInArray,node);
                arr[rank] = null;
                rank += 1;
                node = root;
            }
            if (rank <= arr.length-1)
                arr[rank] = node;
        }
        int firstNotNull = 0;
        while (arr[firstNotNull] == null){
            firstNotNull++;
        }
        first = arr[firstNotNull];
        HeapNode prev = first;
        for (int i=firstNotNull+1; i < arr.length; i++)
        {
            if (arr[i] != null)
            {
                prev.setNext(arr[i]);
                arr[i].setPrev(prev);
                prev = arr[i];
            }
        }
        first.setPrev(prev);
        prev.setNext(first);

    }

   /**
    * public HeapNode findMin()
    *
    * Returns the node of the heap whose key is minimal, or null if the heap is empty.
    *
    */
    public HeapNode findMin()
    {
        return min;
    } 
    
   /**
    * public void meld (FibonacciHeap heap2)
    *
    * Melds heap2 with the current heap.
    *
    */
    public void meld (FibonacciHeap heap2)
    {
        if (heap2.isEmpty())
            return;
        if (this.isEmpty())
        {
            this.first = heap2.first;
            this.min = heap2.min;
            this.numOfNodes = heap2.numOfNodes;
            this.marked = heap2.marked;
            this.numOfTrees = heap2.numOfTrees;
            return;
        }
        if (this.min.getKey()>heap2.min.getKey())
            this.min = heap2.min;
        HeapNode lastNode1 = this.first.prev;
        HeapNode lastNode2 = heap2.first.prev;
        heap2.first.setPrev(lastNode1);
        lastNode1.setNext(heap2.first);
        first.setPrev(lastNode2);
        lastNode2.setNext(first);

        numOfNodes += heap2.numOfNodes;
        numOfTrees += heap2.numOfTrees;
        marked += heap2.marked;
    }

   /**
    * public int size()
    *
    * Returns the number of elements in the heap.
    *   
    */
    public int size()
    {
        return numOfNodes;
    }
    	
    /**
    * public int[] countersRep()
    *
    * Return an array of counters. The i-th entry contains the number of trees of order i in the heap.
    * (Note: The size of of the array depends on the maximum order of a tree.)  
    * 
    */
    public int[] countersRep()
    {
        if (numOfNodes == 0)
            return new int[0];

        int max = first.getRank();
        HeapNode node = first.getNext();
        while (node != null && node != first)
        {
            if (node.getRank()>max)
                max = node.getRank();
            node = node.getNext();
        }
    	int[] arr = new int[max+1];

        arr[first.getRank()] += 1;
        HeapNode node1 = first.getNext();
        while (node1 != null && node1 != first)
        {
            arr[node1.getRank()] += 1;
            node1 = node1.getNext();
        }
        return arr;
    }
	
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap.
	* It is assumed that x indeed belongs to the heap.
    *
    */
    public void delete(HeapNode x) 
    {
        decreaseKey(x,Integer.MIN_VALUE);
        deleteMin();
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * Decreases the key of the node x by a non-negative value delta. The structure of the heap should be updated
    * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
    */
    public void decreaseKey(HeapNode x, int delta)
    {
        if(delta == Integer.MIN_VALUE){
            x.setKey(Integer.MIN_VALUE);
        }
        else {
            x.setKey(x.getKey() - delta);
        }
        if (!x.isRoot() && x.getKey() < x.getParent().getKey()){
            cascadingCut(x,x.getParent());
        }
        if(x.getKey() < this.min.getKey()){
            this.min = x;
        }
    }
    /**
     * public void cascadingCut(HeapNode x, HeapNode y)
     *
     * preform a cascading cut starting at x
     */
    public void cascadingCut(HeapNode x,HeapNode y){
        cut(x,y);
        if(!y.isRoot()){
            if(!y.getMarked()){
                y.setMarked(true);
                marked += 1;
            }
            else{
                this.cascadingCut(y,y.parent);
            }
        }
    }
    /**
     * public void cut(HeapNode x, int delta)
     *
     * cuts x from y, updates all pointers
     */
    public void cut(HeapNode x, HeapNode y){
        x.setParent(null);
        if (x.getMarked())
        {
            x.setMarked(false);
            marked -= 1;
        }
        y.setRank(y.getRank()-1);
        if(x.getNext() == x) {
            y.setChild(null);
        }
        else{
            if(y.getChild() == x){
                y.setChild(x.getNext());
            }
            x.getPrev().setNext(x.getNext());
            x.getNext().setPrev(x.getPrev());
        }
        HeapNode prev = first.getPrev();
        prev.setNext(x);
        first.setPrev(x);
        x.setNext(first);
        x.setPrev(prev);
        this.first = x;
        this.numOfTrees++;
        cuts++;
    }

   /**
    * public int nonMarked() 
    *
    * This function returns the current number of non-marked items in the heap
    */
    public int nonMarked() {
        return numOfNodes - marked;
    }

   /**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * 
    * In words: The potential equals to the number of trees in the heap
    * plus twice the number of marked nodes in the heap. 
    */
    public int potential() 
    {    
        return numOfTrees + 2*marked;
    }

   /**
    * public static int totalLinks() 
    *
    * This static function returns the total number of link operations made during the
    * run-time of the program. A link operation is the operation which gets as input two
    * trees of the same rank, and generates a tree of rank bigger by one, by hanging the
    * tree which has larger value in its root under the other tree.
    */
    public static int totalLinks()
    {    
    	return links;
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the
    * run-time of the program. A cut operation is the operation which disconnects a subtree
    * from its parent (during decreaseKey/delete methods). 
    */
    public static int totalCuts()
    {    
    	return cuts;
    }

     /**
    * public static int[] kMin(FibonacciHeap H, int k) 
    *
    * This static function returns the k smallest elements in a Fibonacci heap that contains a single tree.
    * The function should run in O(k*deg(H)). (deg(H) is the degree of the only tree in H.)
    *  
    * ###CRITICAL### : you are NOT allowed to change H. 
    */
    public static int[] kMin(FibonacciHeap H, int k)
    {
        int[] arr = new int[k];
        if(H.isEmpty() || k==0){
            return arr;
        }
        FibonacciHeap MinHeap = new FibonacciHeap();
        HeapNode temp, firstChild, currMin,nextChild;
        currMin = H.getFirst();
        arr[0] = currMin.getKey();
        for (int i=1; i<k; i++){
            firstChild = currMin.getChild();
            if(firstChild != null) {
                nextChild = firstChild.getNext();
                temp = MinHeap.insert(firstChild.getKey());
                temp.same = firstChild;
                while (nextChild != firstChild) {
                    temp = MinHeap.insert(nextChild.getKey());
                    temp.same = nextChild;
                    nextChild = nextChild.getNext();
                }
            }
            currMin = MinHeap.findMin().same;
            arr[i] = currMin.getKey();
            MinHeap.deleteMin();
        }
        return arr;
    }



   /**
    * public class HeapNode
    * 
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in another file. 
    *  
    */
    public static class HeapNode{

    	public int key;
        public int rank;
        public boolean marked;
        public HeapNode child;
        public HeapNode next;
        public HeapNode prev;
        public HeapNode parent;
        public HeapNode same = null;

    	public HeapNode(int key) {
    		this.key = key;
    	}

    	public int getKey() {
    		return this.key;
    	}
        public int getRank() {
           return this.rank;
       }
        public boolean getMarked() {
           return this.marked;
       }
        public HeapNode getChild() {
           return this.child;
       }
        public HeapNode getNext() {
           return this.next;
       }
        public HeapNode getPrev() {
           return this.prev;
       }
        public HeapNode getParent() {
           return this.parent;
       }

       public void setKey(int key) {
           this.key = key;
       }
       public void setRank(int rank) {
           this.rank = rank;
       }
       public void setMarked(boolean marked) {
           this.marked = marked;
       }
       public void setChild(HeapNode child) {
           this.child = child;
       }
       public void setNext(HeapNode next) {
           this.next = next;
       }
       public void setPrev(HeapNode prev) {
           this.prev = prev;
       }
       public void setParent(HeapNode parent) {
           this.parent = parent;
       }

       public boolean isRoot()
       {
           return this.parent == null;
       }


   }
}
