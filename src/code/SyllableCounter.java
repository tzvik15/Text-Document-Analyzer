package code;
//////////////////////////////////////////////////////////////////////////////
// Adapted from https://www.delftstack.com/howto/java/syllable-counter-in-java
//////////////////////////////////////////////////////////////////////////////

// Note, this syllable counter does not count 100% accurately.
// I don't think any of them do. I think it's good enough, though.

public class SyllableCounter {

    public double countSyllables(String s) {
        double count = 0;
        s = s.toLowerCase();

        boolean isPrevVowel = false;
    
        for (int j = 0; j < s.length(); j++) {
            if (s.contains("a") || s.contains("e") || s.contains("i") || s.contains("o") || s.contains("u") || s.contains("y")) {
                // checking if character is a vowel and if the last letter of the word is 'e' or not
                if (isVowel(s.charAt(j)) && !((s.charAt(j) == 'e') && (j == s.length()-1))) {
                    if (isPrevVowel == false) {
                        count++;
                        isPrevVowel = true;
                    }
                } else {
                    isPrevVowel = false;
                }
            } else {
                count++;
                break;
            }
        }
        return count;
    }

    static public boolean isVowel(char c) {
        if (c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u') {
            return true;
        } else {
            return false;
        }
    }
}
