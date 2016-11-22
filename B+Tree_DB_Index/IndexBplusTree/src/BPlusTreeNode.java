import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

abstract class BPlusTreeNode <key extends Comparable<key>> implements Serializable{

	
	private static final long serialVersionUID = 1L;
	
	protected Object[] keys;
	protected int keyCount;
	protected Long parentNode;
	protected Long leftSibling;
	protected Long rightSibling;
	protected Long myLocation;

	protected BPlusTreeNode() {
		this.keyCount = 0;
		this.parentNode = null;
		this.leftSibling = null;
		this.rightSibling = null;
	}

	public int getKeyCount() {
		return this.keyCount;
	}
	
	@SuppressWarnings("unchecked")
	public key getKey(int index) {
		return (key)this.keys[index];
	}

	public void setKey(int index, key key) {
		this.keys[index] = key;
	}

	public Long getParent() {
		return this.parentNode;
	}

	public void setParent(Long parent) {
		this.parentNode = parent;
	}	
	
	public abstract int getNodeType();
	
	public abstract int search(key key);
	
	
	
	/* The codes below are used to support insertion operation */
	
	public boolean isOverflow() {
		return this.getKeyCount() == this.keys.length;
	}
	
	public BPlusTreeNode<key> dealOverflow() {
		int midIndex = this.getKeyCount() / 2;
		key upKey = this.getKey(midIndex);
		
		BPlusTreeNode<key> newRNode = this.split();
		FileAcessUtility.writeObjectAtPosition(this, this.myLocation);
		
		if (this.getParent() == null) {
			//this.setParent(new BPlusTreeRefNode<key>());
			BPlusTreeRefNode<key> refNode = new BPlusTreeRefNode<key>();
			refNode.setMyLocation(FileAcessUtility.getEndOfFileSeek());
			FileAcessUtility.writeObjectAtEOF(refNode);
			this.setParent(refNode.getMyLocation());
		}
		newRNode.setParent(this.getParent());
		
		// maintain links of sibling nodes
		newRNode.setLeftSibling(this.myLocation);
		newRNode.setRightSibling(this.rightSibling);
		if (this.getRightSibling() != null)
		{
			//this.getRightSibling().setLeftSibling(newRNode);
			BPlusTreeNode<key> tempNode = (BPlusTreeNode<key>)FileAcessUtility.getObjectOnLocation(this.getRightSibling());
			tempNode.setLeftSibling(newRNode.getMyLocation());
			FileAcessUtility.writeObjectAtPosition(newRNode, tempNode.getMyLocation());
		}
		this.setRightSibling(newRNode.getMyLocation());
		FileAcessUtility.writeObjectAtPosition(this, this.getMyLocation());
		// push up a key to parent internal node
		
		
		BPlusTreeNode<key> tmpObj = (BPlusTreeNode<key>)FileAcessUtility.getObjectOnLocation(this.getParent());
		tmpObj = tmpObj.pushUpKey(upKey, this, newRNode);
		
		//FileAcessUtility.writeObjectAtPosition(tmpObj, tmpObj.getMyLocation());
		FileAcessUtility.writeObjectAtPosition(newRNode, newRNode.getMyLocation());
		FileAcessUtility.writeObjectAtPosition(this, this.getMyLocation());
		
		return tmpObj;
	}
	
	protected abstract BPlusTreeNode<key> split();
	
	protected abstract BPlusTreeNode<key> pushUpKey(key key, BPlusTreeNode<key> leftChild, BPlusTreeNode<key> rightNode);
	
	
	

	public Long getLeftSibling() {
		if (this.leftSibling != null && ((BPlusTreeNode<key>)FileAcessUtility.getObjectOnLocation(this.leftSibling)).getParent() == this.getParent())
			return this.leftSibling;
		return null;
	}

	public void setLeftSibling(Long sibling) {
		this.leftSibling = sibling;
	}

	public Long getRightSibling() {
		if (this.rightSibling != null && ((BPlusTreeNode<key>)FileAcessUtility.getObjectOnLocation(this.rightSibling)).getParent() == this.getParent())
			return this.rightSibling;
		return null;
	}

	public void setRightSibling(Long silbling) {
		this.rightSibling = silbling;
	}
	

	public Long getMyLocation() {
		return myLocation;
	}

	public void setMyLocation(Long myLocation) {
		this.myLocation = myLocation;
	}
	
	public void allocateMemory()
	{
		this.myLocation = FileAcessUtility.writeObjectAtEOF(this);
	}
	
}

/**
 * 
 * @version 1.0
 * @author Varun Dani
 *
 * @param <key>
 */
class BPlusTreeRefNode<key extends Comparable<key>> extends BPlusTreeNode<key> implements Serializable{


	private static final long serialVersionUID = 1L;
	
	protected final static int INNERORDER = 10;
	protected Long[] children; 
	
	public BPlusTreeRefNode() {
		this.keys = new Object[INNERORDER + 1];
		this.children = new Long[INNERORDER + 2];
	}
	
	public Long getChild(int index) {
		return this.children[index];
	}

	public void setChild(int index, Long child) {
		this.children[index] = child;
		
		try{
			BPlusTreeNode<key> childObj = (BPlusTreeNode<key>)FileAcessUtility.getObjectOnLocation(child);
			childObj.setParent(this.myLocation);
			FileAcessUtility.writeObjectAtPosition(childObj, childObj.getMyLocation());
		}
		catch(Exception e)
		{
			
		}
		/*if (child != null)
			child.setParent();*/
	} 
	
	@Override
	public int getNodeType() {
		return 0;
	}
	
	@Override
	public int search(key key) {
		int index = 0;
		for (index = 0; index < this.getKeyCount(); ++index) {
			int cmp = this.getKey(index).compareTo(key);
			if (cmp == 0) {
				return index + 1;
			}
			else if (cmp > 0) {
				return index;
			}
		}
		
		return index;
	}
	
	
	
	private void insertAt(int index, key key, BPlusTreeNode<key> leftChild, BPlusTreeNode<key> rightChild) {
		// move space for the new key
		for (int i = this.getKeyCount() + 1; i > index; --i) {
			this.setChild(i, this.getChild(i - 1));
		}
		for (int i = this.getKeyCount(); i > index; --i) {
			this.setKey(i, this.getKey(i - 1));
		}
		
		// insert the new key
		this.setKey(index, key);
		this.setChild(index, leftChild.getMyLocation());
		this.setChild(index + 1, rightChild.getMyLocation());
		this.keyCount += 1;
	}
	
	/**
	 * When splits a internal node, the middle key is kicked out and be pushed to parent node.
	 */
	@Override
	protected BPlusTreeNode<key> split() {
		int midIndex = this.getKeyCount() / 2;
		
		BPlusTreeRefNode<key> newRNode = new BPlusTreeRefNode<key>();
		Long location = FileAcessUtility.writeObjectAtEOF(newRNode);
		newRNode.setMyLocation(location);
		
		for (int i = midIndex + 1; i < this.getKeyCount(); ++i) {
			newRNode.setKey(i - midIndex - 1, this.getKey(i));
			this.setKey(i, null);
		}
		BPlusTreeNode<key> childObj = null;
		for (int i = midIndex + 1; i <= this.getKeyCount(); ++i) {
			newRNode.setChild(i - midIndex - 1, this.getChild(i));
			//newRNode.getChild(i - midIndex - 1).setParent(newRNode);
			childObj = (BPlusTreeNode<key>)FileAcessUtility.getObjectOnLocation(newRNode.getChild(i - midIndex - 1));
			childObj.setParent(newRNode.getMyLocation());
			this.setChild(i, null);
		}
		this.setKey(midIndex, null);
		newRNode.keyCount = this.getKeyCount() - midIndex - 1;
		this.keyCount = midIndex;
		
		return newRNode;
	}
	
	@Override
	protected BPlusTreeNode<key> pushUpKey(key key, BPlusTreeNode<key> leftChild, BPlusTreeNode<key> rightNode) {
		// find the target position of the new key
		int index = this.search(key);
		
		// insert the new key
		this.insertAt(index, key, leftChild, rightNode);
		FileAcessUtility.writeObjectAtPosition(this, this.myLocation);
		// check whether current node need to be split
		if (this.isOverflow()) {
			return this.dealOverflow();
		}
		else {
			return this.getParent() == null ? this : null;
		}
	}

	
}



/**
 * 
 * @version 1.0
 * @author Varun Dani
 *
 * @param <key>
 * @param <value>
 */
class BPlusTreeDataNode<key extends Comparable<key>, value> extends BPlusTreeNode<key> implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	protected final static int LEAFORDER = 10;
	private Object[] values;
	
	public BPlusTreeDataNode() {
		this.keys = new Object[LEAFORDER + 1];
		this.values = new Object[LEAFORDER + 1];
	}

	
	@SuppressWarnings("unchecked")
	public value getValue(int index) {
		return (value)this.values[index];
	}

	public void setValue(int index, value value) {
		this.values[index] = value;
	}
	
	@Override
	public int getNodeType() {
		return 1;
	}
	
	@Override
	public int search(key key) {
		for (int i = 0; i < this.getKeyCount(); ++i) {
			 int cmp = this.getKey(i).compareTo(key);
			 if (cmp == 0) {
				 return i;
			 }
			 else if (cmp > 0) {
				 return -1;
			 }
		}
		
		return -1;
	}
	
	
	/* The codes below are used to support insertion operation */
	
	public void insertKey(key key, value value) {
		int index = 0;
		while (index < this.getKeyCount() && this.getKey(index).compareTo(key) < 0)
			++index;
		this.insertAt(index, key, value);
		FileAcessUtility.writeObjectAtPosition(this, this.myLocation);
	}
	
	private void insertAt(int index, key key, value value) {
		// move space for the new key
		for (int i = this.getKeyCount() - 1; i >= index; --i) {
			this.setKey(i + 1, this.getKey(i));
			this.setValue(i + 1, this.getValue(i));
		}
		
		// insert new key and value
		this.setKey(index, key);
		this.setValue(index, value);
		++this.keyCount;
	}
	
	
	/**
	 * When splits a leaf node, the middle key is kept on new node and be pushed to parent node.
	 */
	@Override
	protected BPlusTreeNode<key> split() {
		int midIndex = this.getKeyCount() / 2;
		
		BPlusTreeDataNode<key, value> newRNode = new BPlusTreeDataNode<key, value>();
		Long location = FileAcessUtility.writeObjectAtEOF(newRNode);
		newRNode.setMyLocation(location);
		
		for (int i = midIndex; i < this.getKeyCount(); ++i) {
			newRNode.setKey(i - midIndex, this.getKey(i));
			newRNode.setValue(i - midIndex, this.getValue(i));
			this.setKey(i, null);
			this.setValue(i, null);
		}
		newRNode.keyCount = this.getKeyCount() - midIndex;
		this.keyCount = midIndex;
		
		return newRNode;
	}
	
	@Override
	protected BPlusTreeNode<key> pushUpKey(key key, BPlusTreeNode<key> leftChild, BPlusTreeNode<key> rightNode) {
		throw new UnsupportedOperationException();
	}
}
class BPlusValueClass extends TreeMap<String,Long> implements Serializable
{
private static final long serialVersionUID = 1L;
public Long[] getListOfValues(String fromval,int counter)
{
	Long ValeObject = FileAcessUtility.getHeaderBlock();
	int length = FileAcessUtility.getHeaderBlockInt();
	BPlusValueClass valueClass = (BPlusValueClass)FileAcessUtility.getObjectOnLocationofSize(ValeObject,length);
	boolean printIt = false;
	Long[] locationValues = new Long[counter];
	int i = 0;
	for(Map.Entry<String,Long> aa : valueClass.entrySet())
	{
		if(aa.getKey().equals(fromval))printIt = true;
		if(printIt && counter>0)
		{
			locationValues[i++]=aa.getValue();
			counter--;
		}
	}
	return locationValues;
}
public void insertValueinBPlusTree(String key,Long value)
{

	Long ValeObject = FileAcessUtility.getHeaderBlock();
	int length = FileAcessUtility.getHeaderBlockInt();
	BPlusValueClass valueClass = (BPlusValueClass)FileAcessUtility.getObjectOnLocationofSize(ValeObject,length);
	valueClass.put(key, value);
	
	int newLen = FileAcessUtility.writeObjectBlock(valueClass,ValeObject);
	FileAcessUtility.writeHeaderBlockInt(newLen, 520L);
	
}
}
