package filesystem;

import java.util.ArrayList;

public class DirectoryNode implements Element{
    private String name;
    private ArrayList<Element> children = new ArrayList<Element>();

    public DirectoryNode(String name){
        this.name = name;
    }

    public DirectoryNode addElement(Element e){
        children.add(e);
        return this;
    }

    public ArrayList<Element> getChildren() {
        return children;
    }

    public String getName() {
        return name;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
