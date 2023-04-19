import entity.Rope;

public class Combiner {
    public static void main(String args[]){
        Rope original = new Rope("","");
        Rope left = new Rope("","");
        left.setLeft(new Rope("I","orig"));
        left.setRight(new Rope("am","orig"));
        original.setLeft(left);
        original.setRight(new Rope("good","orig"));

        Rope Alice = cloneRope(original);
        Alice.setRight(new Rope("hungry","Alice"));
        Rope Bob = cloneRope(original);
        Bob.setRight(new Rope("full","Bob"));
        Rope Alice_v2 = mergeRope(Alice, Bob, original);
        Rope Charlie = cloneRope(original);
        Charlie.setRight(new Rope("eating","Charlie"));
        Rope Charlie_v2 = mergeRope(Charlie, Bob, original);
        Rope Charlie_v3 = mergeRope(Charlie_v2, Alice_v2, Bob);
    }


    public static Rope mergeRope(Rope user1, Rope user2, Rope original){
        if(user1.getHashVal().equals(user2.getHashVal())) return user1;
        if(original.getHashVal().equals(user1.getHashVal())) return cloneRope(user2);
        if(original.getHashVal().equals(user2.getHashVal()))  return user1;
        return merge(cloneRope(user1), user2);
    }

    public static Rope cloneRope(Rope rope){
        if(rope == null)    return null;
        Rope clonedRope = new Rope(rope.getData(),rope.getUserDesc());
        clonedRope.setLeft(cloneRope(rope.getLeft()));
        clonedRope.setRight(cloneRope(rope.getRight()));
        return clonedRope;
    }

    public static Rope merge(Rope user1, Rope user2){
        if(user1 == null && user2 == null)  return user1;
        if(user1 == null){
            return user2;
        }
        else if(user2 == null){
            return user1;
        }
        if(user1.getHashVal().equals(user2.getHashVal())) return user1;
        user1.setLeft(merge(user1.getLeft(), user2.getLeft()));
        String temp = user1.getData();
        temp = (temp.length() == 0 || user1.getData().equals(user2.getData())) ? user2.getData() : temp + " " + user2.getData();
        user1.setData(temp);
        String userDesc = user1.getUserDesc().equals(user2.getUserDesc()) ? user1.getUserDesc() : user1.getUserDesc() + " " + user2.getUserDesc();
        user1.setUserDesc(userDesc);
        user1.setRight(merge(user1.getRight(), user2.getRight()));
        segregate(user1);
        return user1;
    }

    public static void segregate(Rope user){
        if(user == null)    return;
        segregate(user.getLeft());
        String[] data = user.getData().split(" ");
        String[] userDesc = user.getUserDesc().split(" ");
        if(data.length>1){
            if(user.getLeft() == null)  user.setLeft(new Rope(data[0], userDesc[0]));
            if(user.getRight() == null)  user.setRight(new Rope(data[1],userDesc[1]));
            user.setData("");
            user.setUserDesc("");
        }
        //Need to set length for all level of nodes(as length will change)
        user.setLength();
        segregate(user.getRight());
    }
}
