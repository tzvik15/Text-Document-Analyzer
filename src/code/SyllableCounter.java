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

        //checks if current word contains a vowel, if not syllable count will be one.
        if (s.contains("a") || s.contains("e") || s.contains("i") || s.contains("o") || s.contains("u") || s.contains("y")) {
        	//for loop to parse the word if a vowel is found in word
            for (int j = 0; j < s.length(); j++) {
            	// checking if character is a vowel and if the last letter of the word is 'e' or not
                if (isVowel(s.charAt(j))) {
                	count++;
                }
                //checks if character is equal to e
                else if(s.charAt(j) =='e') {
                		//check if e is not the last letter
                		if(!(j == s.length()-1)) {
                			//check if e is not the second to last letter and last letter equals d
                			if(!((j == s.length()-2) && (s.charAt(j+1)=='d'))){	
                			count++;
                			}
                		}
                	}
            }
            //if word doesn't contain a vowel, we can count word as one syllable (since all words have at least one syllable)
        } else {
                count++;
            }
        return count;
    }

    static public boolean isVowel(char c) {
        if (c == 'a' || c == 'i' || c == 'o' || c == 'u') {
            return true;
        } else {
            return false;
        }
    }
}
