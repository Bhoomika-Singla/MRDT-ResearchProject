import com.google.gson.Gson;
import entity.BenchmarkData;
import entity.BenchmarkData.Transaction;
import entity.Rope;
import javafx.util.Pair;

import java.util.*;
import java.util.zip.GZIPInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class testBenchmark{
    public static void main(String[] args) {
        Benchmarking benchmarking = new Benchmarking();

        benchmarking.main();

    }

    public static class Benchmarking<T>{
        RopeStructureMerger ropeMerger = new RopeStructureMerger();
        public void main() {

            //Fetch endContent, numAgents and txns from the file

            //Store map of versions and agent (will populate it while going through each transaction)

            //Form an arraylist of size numAgents, arraylist would store arraylist of Rope (denoting various versions)

            //For each transaction, check if parents is empty or has a single entry or has more than 2 entries:
            //If parent is empty, create a new rope from a preexisting rope and add patches on it (using index) and add to the arraylist at index agent


            //If parent has single entry, create a new rope from a older rope version(find agent from parent version and identify which rope in agent arraylist corresponds to this version) and add patches on it (using index).
            //Store this rope to arraylist at index agent


            //If the parent has more than or equal to 2 entries -> create a rope from all the rope versions (identified using map similar to case of single agent).
            // Apply predefined merge algorithm passing in three parameters : original (find LCA of the versions through graph and use that rope version(must be identified in similar manner),
            //all the parent rope versions passed in two at a time

            //Apply patches to the merged rope and store this rope to arraylist at index agent

            //Repeat it for all transactions

            //once all transactions are completed, create a rope from endContent and compare the last version of rope(done by any of the agents) with the endContent rope

            //Read the file json gz file
            String filePath = "conf/friendsforever.json.gz";
            try {
                GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(filePath));
                Reader reader = new InputStreamReader(gzip);
                Gson gson = new Gson();

                // Assuming you have a class that matches the JSON structure
                BenchmarkData data = gson.fromJson(reader, BenchmarkData.class);

                // Fetch endContent, numAgents, and txns from the file
                String endContent = data.getEndContent();
                int numAgents = data.getNumAgents();
                ArrayList<Transaction> txns = data.getTxns();

                // Store map of versions and agent <TXN_no, <agent, version_no>>
                HashMap<Integer, Pair<Integer, Integer>> versionMap = new HashMap<>();

                // ArrayList of ArrayList<Rope>
                List<List<Rope>> agentRopes = new ArrayList<>();
                for (int i = 0; i < numAgents; i++) {
                    agentRopes.add(new ArrayList<>());
                }

                // Process each transaction
                for (int i = 0; i<txns.size(); i++) {
                    Transaction txn = txns.get(i);
                    Rope rope;
                    // Processing transactions : 1. Check parents 2. Create new ropes 3. Apply patches
                    // 4. Merge ropes if needed
                    if(txns.get(i).getParents().size() == 0){
                        //Since no parent, start with empty rope
                        rope = new Rope("", "");
                        List<List<T>> patches = txn.getPatches();
                        for(List<T> p : patches){
                            //pos, delHere, insContent -> use pos and delHere to figure content
                            rope = applyPatch((Double)p.get(0), (Double)p.get(1), (String)p.get(2), rope);
                        }
                    } else if(txn.getParents().size() == 1){
                        Pair<Integer, Integer> agentRopeVersion = versionMap.get(txn.getParents().get(0));
                        rope = agentRopes.get(agentRopeVersion.getKey()).get(agentRopeVersion.getValue());
                        List<List<T>> patches = txn.getPatches();
                        for(List<T> p : patches){
                            //pos, delHere, insContent -> use pos and delHere to figure content
                            rope = applyPatch((Double)p.get(0), (Double)p.get(1), (String)p.get(2), rope);
                        }
                    } else{
                        Pair<Integer, Integer> agentRopeVersion1 = versionMap.get(txn.getParents().get(0));
                        Rope rope1 = agentRopes.get(agentRopeVersion1.getKey()).get(agentRopeVersion1.getValue());
                        Pair<Integer, Integer> agentRopeVersion2 = versionMap.get(txn.getParents().get(1));
                        Rope rope2 = agentRopes.get(agentRopeVersion2.getKey()).get(agentRopeVersion2.getValue());
                        Integer lcaTxn = findLCA(txns.get((Integer)txn.getParents().get(0)), txns.get((Integer)txn.getParents().get(1)), txns);
//                        Transaction common_txn = txns.get(lcaTxn);
                        Pair<Integer, Integer> originalRopeVersion = versionMap.get(lcaTxn);
                        Rope original = agentRopes.get(originalRopeVersion.getKey()).get(originalRopeVersion.getValue());
                        rope = ropeMerger.merge(rope1, rope2, original);
//                        rope= null;

                    }


                    //Store new rope version in agent list
                    List<Rope> ropeVersions = agentRopes.get(txn.getAgent());
                    ropeVersions.add(rope);
                    agentRopes.set(txn.getAgent(), ropeVersions);
                    //Store versions to track in version map
                    versionMap.put(i, new Pair<>(txn.getAgent(), ropeVersions.size()-1));

                }

                // Compare the last version of rope with endContent
                Rope finalRope = null; // TODO: get the final rope version;
                if (finalRope.toString().equals(endContent)) {
                    System.out.println("Benchmark successful!");
                } else {
                    System.out.println("Mismatch in final content.");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private Rope applyPatch(Double pos, Double delHere, String insContent, Rope rope){
            //need to apply in the same way as a typical rope
            String ropeString = ropeToString(rope);
            String before = ropeString.substring(0, pos.intValue());
            String after = ropeString.substring((pos).intValue() + (delHere).intValue());
            ropeString = before + insContent + after;

            return formRope(ropeString);

//            Rope left = rope.split(0, pos);
//            Rope right = rope.split(pos+delHere);
//            Rope result = new Rope("","");
//            result.setLeft(left);
////            TODO: Complete form rope through content
////            result.setRight(formRope(insContent));
//
//            Rope finalRope = new Rope("", "");
//            finalRope.setLeft(result);
//            finalRope.setRight(right);
//            return finalRope;
        }

        //TODO: Implement it
        private String ropeToString(Rope rope){
            StringBuilder sb = new StringBuilder();
            deconstructRope(rope, sb);
            return sb.toString();
        }

        private void deconstructRope(Rope rope, StringBuilder sb){
            if(rope == null)    return;
            deconstructRope(rope.getLeft(), sb);
            deconstructRope(rope.getRight(), sb);
            if(rope != null)    sb.append(rope.getData());
        }

        private Rope formRope(String content){
            String[] parts = content.split("/(?=\s)/");
//            String[] parts = content.split("(?<=\\b| )");
            return buildRope(parts, 0, parts.length - 1);
        }

        private Rope buildRope(String[] parts, int start, int end) {
            if (start > end) {
                return null;
            }
            if (start == end) {
                return new Rope(parts[start], "");
            }

            int mid = (start + end) / 2;
            Rope node = new Rope("",""); // Internal node

            node.setLeft(buildRope(parts, start, mid));
            node.setRight(buildRope(parts, mid + 1, end));

            return node;
        }

        private Integer findLCA(Transaction txn1, Transaction txn2, List<Transaction> txns){
            Set<Integer> ancestors1 = new HashSet<>();
            Set<Integer> ancestors2 = new HashSet<>();

            findAllAncestors(txn1, ancestors1, txns);
            findAllAncestors(txn2, ancestors2, txns);

            ancestors1.retainAll(ancestors2); // Find common ancestors

            if (ancestors1.isEmpty()) return null;

            Integer lca = null;
            int maxDepth = Integer.MIN_VALUE;
            for (Integer ancestor : ancestors1) {
                Set<Integer> tempAncestors = new HashSet<>();
//                int index = txns.indexOf(ancestor);
                findAllAncestors(txns.get(ancestor), tempAncestors, txns);
                int depth = tempAncestors.size();
                if (depth > maxDepth) {
                    maxDepth = depth;
                    lca = ancestor;
                }
            }

            return lca;
        }

        private void findAllAncestors(Transaction start, Set<Integer> ancestors, List<Transaction> txns) {
            List<Integer> parents = start.getParents();
            for(Integer par : parents){
                ancestors.add(par);
//                int index = txns.indexOf(par);
                findAllAncestors(txns.get(par), ancestors, txns);
            }
        }

//    private static void applyPatches(String startContent, String endContent, List<JsonNode> txns) {
//        String content = startContent;
//
//        System.out.println("applying " + txns.size() + " txns...");
//        long startTime = System.currentTimeMillis();
//        long lastTime = 0;
//
//        for (int i = 0; i < txns.size(); i++) {
//            if (i % 10000 == 0) {
//                System.out.println(i);
//            }
//
//            JsonNode txn = txns.get(i);
//            long time = txn.get("time").asLong();
//            List<JsonNode> patches = txn.findValues("patches");
//
//            assert time != 0;
//            assert patches != null;
//            assert patches.size() > 0;
//
//            long t = new Date(time).getTime();
//            assert t >= lastTime;
//            lastTime = t;
//
//            for (JsonNode patch : patches) {
//                int pos = patch.get(0).asInt();
//                int delHere = patch.get(1).asInt();
//                String insContent = patch.get(2).asText();
//
//                assert content.length() >= pos + delHere;
//
//                String before = content.substring(0, pos);
//                String after = content.substring(pos + delHere);
//                content = before + insContent + after;
//            }
//        }
//
//        long endTime = System.currentTimeMillis();
//        System.out.println("apply time: " + (endTime - startTime) + " ms");
//
//        assert content.equals(endContent);
//    }
    }
}

