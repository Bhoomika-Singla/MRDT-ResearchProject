import entity.Rope;

import java.util.*;

/**
 * Remaining pieces for "Integration/Modification":
 * 1. Replace LCS with 2 way merge (need to just implement with a modifications)
 * 2. Modify postorder traversal to include strings instead of integers (would allow addition of unique word identifiers
 *    -> can use "-" for adding identifiers in string)
 * 3. Need to understand how the integer -> string conversion would impact parentChildMap
 * 4. Might also have to modify rope structure a bit to handle correct hash updates for left and right nodes
 * 5. Might also need a way to directly insert/remove from rope -> See if needed {may or may not be}
 * 6. And lastly, see if you can do large scale benchmark testing to prove correctness (and also write algo in pseudocode form)
 * (maybe have two identifiers and store them in an object and use that instead of actual integer?)
 * (just an implementation thing doesn't actually impact the algorithm)
 *
 **/
public class FinalAlgorithm {
    private class ropeObject{
        Rope rope;
        Integer hash;
        Integer identifier;

        @Override
        public int hashCode() {
            return Objects.hash(hash, identifier);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ropeObject)) return false;
            ropeObject that = (ropeObject) o;
            return hash.equals(that.hash) && identifier.equals(that.identifier);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }


    //main function -> modify parent child map
    public Rope merge(Rope rope1, Rope rope2, Rope original){
        //Map of parent and children
        Map<Integer, List<Integer>> parentChildMap = new HashMap<>();

        Map<Integer, Rope> ropehashMap = new HashMap<>();

        List<Integer> O_list = new ArrayList<>();
        List<Integer> A_list = new ArrayList<>();
        List<Integer> B_list = new ArrayList<>();

        postorder(original, O_list, parentChildMap, ropehashMap);
        postorder(rope1, A_list, parentChildMap, ropehashMap);
        postorder(rope2, B_list, parentChildMap, ropehashMap);

        Set<Integer> finalSet = calculateFinalSet(new LinkedHashSet<>(O_list), new LinkedHashSet<>(A_list), new LinkedHashSet<>(B_list), parentChildMap);
        List<Integer> finalMergeOrder = merge(finalSet, O_list, A_list, B_list);

        return formRope(finalMergeOrder, ropehashMap);
    }

    /**
     * form functions and then fill in those functions -> not too much work (already though through) -> just execution pending
     * need to complete it by today, so we can show him tomorrow
     * need to figure out a plan for tomorrow
     * */

    public void postorder(Rope rope, List<Integer> result, Map<Integer, List<Integer>> parentChildMap, Map<Integer, Rope> ropehashMap){
        if(rope.getLeft() != null)  postorder(rope.getLeft(), result, parentChildMap, ropehashMap);
        if(rope.getRight() != null) postorder(rope.getRight(), result, parentChildMap, ropehashMap);
        result.add(rope.hashCode());
        List<Integer> children = parentChildMap.getOrDefault(rope.hashCode(), new ArrayList<>());
        if(rope.getLeft() != null && children.indexOf(rope.getLeft().hashCode()) == -1)  children.add(rope.getLeft().hashCode());
        if(rope.getRight() != null && children.indexOf(rope.getRight().hashCode()) == -1)  children.add(rope.getRight().hashCode());
        parentChildMap.put(rope.hashCode(), children);
        ropehashMap.put(rope.hashCode(), rope);
    }

    public Set<Integer> setDifference(Set<Integer> set1, Set<Integer> set2) {
        Set<Integer> result = new HashSet<>(set1);
        result.removeAll(set2);
        return result;
    }

    // Function to calculate the final set O'''
    public Set<Integer> calculateFinalSet(Set<Integer> O, Set<Integer> A, Set<Integer> B, Map<Integer, List<Integer>> parentChildMap) {
        Set<Integer> O_minus_A = setDifference(O, A);
        Set<Integer> O_prime = setDifference(O, O_minus_A);
        Set<Integer> A_prime = setDifference(A, O);

        Set<Integer> O_prime_minus_B = setDifference(O_prime, B);
        Set<Integer> O_double_prime = setDifference(O_prime, O_prime_minus_B);
        Set<Integer> B_prime = setDifference(B, O);

        //O_optimized contains components that are common, we removed the children nodes that were duplicate
        Set<Integer> O_optimized = new LinkedHashSet<>(O_double_prime);
        Iterator<Integer> iterator = O_optimized.iterator();
        while (iterator.hasNext()) {
            Integer element = iterator.next();
            //check if key present, remove its values
            if (parentChildMap.containsKey(element)) {
                O_optimized.removeAll(parentChildMap.get(element));
            }
        }

        //A_optimized represents components that are not common and must be added as its added by user A
        Set<Integer> A_optimized = new LinkedHashSet<>(A_prime);
        //check if the element is a key then see if all the values that map to it are in set, if yes remove the values or else remove the key
        Iterator<Integer> A_iterator = A_optimized.iterator();
        while (A_iterator.hasNext()) {
            Integer element = A_iterator.next();
            //check if key present, remove its values
            if (parentChildMap.containsKey(element)) {
                if(A_optimized.containsAll(parentChildMap.get(element))){
                    A_optimized.removeAll(parentChildMap.get(element));
                } else{
                    A_optimized.remove(element);
                }
            }
        }

        //B_optimized represents components that are not common and must be added as its added by user B
        Set<Integer> B_optimized = new LinkedHashSet<>(B_prime);
        Iterator<Integer> B_iterator = B_optimized.iterator();
        while (B_iterator.hasNext()) {
            Integer element = B_iterator.next();
            //check if key present, remove its values
            if (parentChildMap.containsKey(element)) {
                if(B_optimized.containsAll(parentChildMap.get(element))){
                    B_optimized.removeAll(parentChildMap.get(element));
                } else{
                    B_optimized.remove(element);
                }
            }
        }
        // Final set O''' is the union of O'', A', and B'
        Set<Integer> O_triple_prime = new HashSet<>(O_optimized);
        O_triple_prime.addAll(A_optimized);
        O_triple_prime.addAll(B_optimized);

        return O_triple_prime;
    }

    // Function to merge based on the longest common subsequence and the unique elements
    public List<Integer> merge(Set<Integer> O_triple_prime, List<Integer> O, List<Integer> A, List<Integer> B) {
        List<Integer> lcs = longestCommonSubsequence(longestCommonSubsequence(O, A), B);
        System.out.println(lcs.toString());

        List<Integer> mergeOrder = new ArrayList<>();

        int indexO = 0, indexA = 0, indexB = 0, indexLCS = 0;
        boolean lcsMatched = false;

        while (indexLCS < lcs.size() || indexO < O.size() || indexA < A.size() || indexB < B.size()) {
            Integer currentLCS = indexLCS < lcs.size() ? lcs.get(indexLCS) : null;

            // Check O structure
            if (indexO < O.size() && !lcsMatched && (currentLCS == null || !O.get(indexO).equals(currentLCS))) {
                if (O_triple_prime.contains(O.get(indexO))) {
                    mergeOrder.add(O.get(indexO));
                }
                indexO++;
            } else if (indexO < O.size() && O.get(indexO).equals(currentLCS)) {
                lcsMatched = true;
            }

            // Check A structure
            if (indexA < A.size() && !lcsMatched && (currentLCS == null || !A.get(indexA).equals(currentLCS))) {
                if (O_triple_prime.contains(A.get(indexA)) && !mergeOrder.contains(A.get(indexA))) {
                    mergeOrder.add(A.get(indexA));
                }
                indexA++;
            } else if (indexA < A.size() && A.get(indexA).equals(currentLCS)) {
                lcsMatched = true;
            }

            // Check B structure
            if (indexB < B.size() && !lcsMatched && (currentLCS == null || !B.get(indexB).equals(currentLCS))) {
                if (O_triple_prime.contains(B.get(indexB)) && !mergeOrder.contains(B.get(indexB))) {
                    mergeOrder.add(B.get(indexB));
                }
                indexB++;
            } else if (indexB < B.size() && B.get(indexB).equals(currentLCS)) {
                lcsMatched = true;
            }

            // If all structures matched the LCS, move to the next LCS element
            if (lcsMatched &&
                    (indexO >= O.size() || O.get(indexO).equals(currentLCS)) &&
                    (indexA >= A.size() || A.get(indexA).equals(currentLCS)) &&
                    (indexB >= B.size() || B.get(indexB).equals(currentLCS))) {
                mergeOrder.add(currentLCS); // Add the LCS element once
                indexO = O.get(indexO).equals(currentLCS) ? indexO + 1 : indexO;
                indexA = A.get(indexA).equals(currentLCS) ? indexA + 1 : indexA;
                indexB = B.get(indexB).equals(currentLCS) ? indexB + 1 : indexB;
                indexLCS++;
                lcsMatched = false; // Reset for next LCS element
            }
        }

        return mergeOrder;
    }


}
