import entity.Rope;

import java.util.*;

public class RopeStructureMerger {

    private List<Integer> longestCommonSubsequence(List<Integer> a, List<Integer> b) {
        int[][] lengths = new int[a.size() + 1][b.size() + 1];

        // row 0 and column 0 are initialized to 0 already

        for (int i = 0; i < a.size(); i++)
            for (int j = 0; j < b.size(); j++)
                if (a.get(i).equals(b.get(j)))
                    lengths[i+1][j+1] = lengths[i][j] + 1;
                else
                    lengths[i+1][j+1] = Math.max(lengths[i+1][j], lengths[i][j+1]);

        // read the substring out from the matrix
        List<Integer> lcs = new ArrayList<>();

        for (int x = a.size(), y = b.size(); x != 0 && y != 0; ) {
            if (lengths[x][y] == lengths[x-1][y])
                x--;
            else if (lengths[x][y] == lengths[x][y-1])
                y--;
            else {
                assert a.get(x-1).equals(b.get(y-1));
                lcs.add(a.get(x-1));
                x--;
                y--;
            }
        }

        Collections.reverse(lcs);
        return lcs;
    }

    // Utility function to calculate set difference
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

    //TODO: Create rope
    public Rope formRope(List<Integer> finalMergeOrder, Map<Integer, Rope> ropehashMap){
        return buildRope(finalMergeOrder, 0, finalMergeOrder.size() - 1, ropehashMap);
    }

    private Rope buildRope(List<Integer> parts, int start, int end, Map<Integer, Rope> ropehashMap) {
        if (start > end) {
            return null;
        }
        if (start == end) {
            return ropehashMap.get(parts.get(start));
        }

        int mid = (start + end) / 2;
        Rope node = new Rope("",""); // Internal node

        node.setLeft(buildRope(parts, start, mid, ropehashMap));
        node.setRight(buildRope(parts, mid + 1, end, ropehashMap));

        return node;
    }

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

    public void main(String[] args) {

        List<String> O = Arrays.asList("H1", "H2", "H3");
        List<String> A = Arrays.asList("H1", "H2", "H4", "H5");
        List<String> B = Arrays.asList("H1", "H2", "H6", "H5");

//        List<String> O = Arrays.asList("H1", "H2", "H3", "H4");
//        List<String> A = Arrays.asList("H1", "H2", "H5", "H4");
//        List<String> B = Arrays.asList("H1", "H2", "H4");
//        List<String> O = Arrays.asList("H1", "H2", "H3", "H4", "H8", "H9");
//        List<String> A = Arrays.asList("H1", "H5", "H3", "H4", "H7", "H9");
//        List<String> B = Arrays.asList("H1", "H2", "H4", "H8", "H9");

//        Set<String> finalSet = calculateFinalSet(new HashSet<>(O), new HashSet<>(A), new HashSet<>(B));
//        List<String> finalMergeOrder = merge(finalSet, O, A, B);

//        System.out.println("Final Structure: " + finalSet);
//        System.out.println("Merge Order: " + finalMergeOrder);
    }


    /*
    * Pending items:
    * 1. Merge algorithm :- LCS -> 2 way merge
    * 2. Rope data structure :- insert and remove functions
    * 3. Manual -> Automated script :- Changing random words to something else
    * 4. Duplicates issue needs to be fixed manually -> need a better way to resolve it -> map/dict?
    *
    * */
}