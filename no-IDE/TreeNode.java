

import java.util.ArrayList;
import java.util.List;

public class TreeNode<T> {
	private TreeNode<T> parent = null;
	private List<TreeNode<T>> children = new ArrayList<>();
	private T element = null;
	
	public TreeNode(TreeNode<T> parent, T element) {
		this.parent = parent;
		this.element = element;
	}

	public void addChild(TreeNode<T> child){
		child.setParent(this);
		this.children.add(child);
	}
	
	public void addChild(T element){
		TreeNode<T> node = new TreeNode<T>(this, element);
		this.children.add(node);
	}
	
	public TreeNode<T> getParent() {
		return parent;
	}

	public void setParent(TreeNode<T> parent) {
		this.parent = parent;
	}

	public List<TreeNode<T>> getChildren() {
		return children;
	}

	public boolean hasChildrenWithElement(T t){
		for(TreeNode<T> n : children){
			if(n.getElement().equals(t))return true;
		}
		return false;
	}
	
	public boolean hasChildren(){
		return !this.children.isEmpty();
	}
	
	public TreeNode<T> getChildAtElement(T t){
		for(TreeNode<T> n : children){
			if(n.getElement().equals(t))return n;
		}
		return null;
	}
	
	public void setChildren(List<TreeNode<T>> children) {
		for(TreeNode<T> t : children) {
            t.setParent(this);
        }
        this.children.addAll(children);
	}

	public T getElement() {
		return element;
	}

	public void setElement(T element) {
		this.element = element;
	}
	
	public void DFS(){
		for(TreeNode<T> n : children){
			if(n.getElement() instanceof Character && (Character)n.getElement() == '#')
				System.out.println(n.getElement());
			else System.out.print(n.getElement());
			n.DFS();
		}
	}	
}

