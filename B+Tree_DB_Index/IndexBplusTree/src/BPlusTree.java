/**
 * 
 * This class is Main B+ Tree that contains main methods and implementation of 
 * Its Two type of Nodes 
 * 
 * @version 1.0
 * @since 1.0
 * @author Varun Dani
 *
 * @param <key>
 * @param <value>
 */
public class BPlusTree<key extends Comparable<key>,value> {

	
	private BPlusTreeNode<key> root;
	
	public BPlusTree(String indexFileName) {
		//this.root = new BPlusTreeDataNode<key, value>();
		
		try {
			//Create File and Write Header on First Block
			root = getObjectOnLocation(FileAcessUtility.getHeaderBlockAddress());
		         
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	/**
	 * Insert new Block in B+ Tree
	 * 
	 * @param key
	 * @param value
	 */
	public void insertIntoBPlusTree(key key, value value) {
		BPlusTreeDataNode<key, value> leafNode = this.findKeyLocation(key);
		leafNode.insertKey(key, value);
		
		FileAcessUtility.writeObjectAtPosition(leafNode, leafNode.getMyLocation());
		
		if (leafNode.isOverflow()) {
			BPlusTreeNode<key> n = leafNode.dealOverflow();
			if (n != null)
			{
				this.root = n;
				FileAcessUtility.setHeaderBlockAddress(n.getMyLocation());
				FileAcessUtility.writeObjectAtPosition(n, n.getMyLocation());
			}
		}
		FileAcessUtility.writeObjectAtPosition(leafNode, leafNode.getMyLocation());
	}
	
	/**
	 * Search value of key from B+ Tree
	 * 
	 * @param key
	 * @return
	 */
	public value searchFromBPlusTree(key key) {
		BPlusTreeDataNode<key, value> leafNode = this.findKeyLocation(key);
		
		int index = leafNode.search(key);
		return (index == -1) ? null : leafNode.getValue(index);
	}
	
	/**
	 * Find Key Location
	 * 
	 * @param key
	 * @return
	 */
	private BPlusTreeDataNode<key, value> findKeyLocation(key key) {
		BPlusTreeNode<key> node = this.root;
		while (node.getNodeType() == 0) {
			node = getObjectOnLocation(((BPlusTreeRefNode<key>)node).getChild(node.search(key)));
		}
		
		return (BPlusTreeDataNode<key, value>)node;
	}
	
	
	private BPlusTreeNode<key> getObjectOnLocation(Long seekLocation)
	{
         try 
         {
        	 BPlusTreeNode<key> tempNode;
        	 tempNode = (BPlusTreeNode<key>)FileAcessUtility.getObjectOnLocation(seekLocation);
        	 if(tempNode!=null)
        	 {
        		 return tempNode;
        	 }
        	 else 
        	 {
        		 throw new Exception();
        	 }
         } catch (Exception e) {
        	 
        	 BPlusTreeDataNode<key, value> newRNode = new BPlusTreeDataNode<key, value>();
        	 newRNode.setMyLocation(FileAcessUtility.getEndOfFileSeek());
        	 FileAcessUtility.writeObjectAtEOF(newRNode);
			 return newRNode;
         }
	}
}
