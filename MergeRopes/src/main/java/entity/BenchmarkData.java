package entity;

import java.util.ArrayList;
import java.util.List;

public class BenchmarkData {
    String endContent = "";
    int numAgents = 0;
    ArrayList<Transaction> txns;

    public String getEndContent() {
        return endContent;
    }

    public void setEndContent(String endContent) {
        this.endContent = endContent;
    }

    public int getNumAgents() {
        return numAgents;
    }

    public void setNumAgents(int numAgents) {
        this.numAgents = numAgents;
    }

    public ArrayList<Transaction> getTxns() {
        return txns;
    }

    public void setTxns(ArrayList<Transaction> txns) {
        this.txns = txns;
    }

    public class Transaction<T>{
        // Marks parent versions
        List<Integer> parents;
        // Marks children for this transaction
        int numChildren;
        //Denotes agent id working in this transaction
        int agent;
        
        //Denotes patches
        List<List<T>> patches;

        public List<Integer> getParents() {
            return parents;
        }

        public void setParents(List<Integer> parents) {
            this.parents = parents;
        }

        public int getNumChildren() {
            return numChildren;
        }

        public void setNumChildren(int numChildren) {
            this.numChildren = numChildren;
        }

        public int getAgent() {
            return agent;
        }

        public void setAgent(int agent) {
            this.agent = agent;
        }

        public List<List<T>> getPatches() {
            return patches;
        }

        public void setPatches(List<List<T>> patches) {
            this.patches = patches;
        }

        public class Patch<T>{
//            List<T> ;
            //Denotes where edit took place
//            int position;
//            int numCharDeleted;
//            String insertedString;
//
//            public int getPosition() {
//                return position;
//            }
//
//            public void setPosition(int position) {
//                this.position = position;
//            }
//
//            public int getNumCharDeleted() {
//                return numCharDeleted;
//            }
//
//            public void setNumCharDeleted(int numCharDeleted) {
//                this.numCharDeleted = numCharDeleted;
//            }
//
//            public String getInsertedString() {
//                return insertedString;
//            }
//
//            public void setInsertedString(String insertedString) {
//                this.insertedString = insertedString;
//            }
        }
    }
}
