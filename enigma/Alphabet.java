package enigma;

import static enigma.EnigmaException.*;

/* Extra Credit Only */

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Chris Jereza
 */
class Alphabet {

    /** A new alphabet containing CHARS.  Character number #k has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        _charsString = chars;
        _chars = new char[chars.length()];
        for (int i = 0; i < _chars.length; i += 1) {
            if (alreadyAdded(chars.charAt(i))) {
                throw new EnigmaException("Alphabet cannot have duplicates.");
            } else {
                _chars[i] = chars.charAt(i);
            }
        }
    }

    /** Returns the size of the alphabet. */
    int size() {
        return _chars.length;
    }

    /** Returns true if C is in this alphabet. */
    boolean contains(char c) {
        for (char x : _chars) {
            if (x == c) {
                return true;
            }
        }
        return false;
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        if (index >= 0 && index < size()) {
            return _chars[index];
        } else {
            throw new EnigmaException("Index out of bounds.");
        }
    }

    /** Returns the index of character C, which must be in the alphabet. */
    int toInt(char c) {
        for (int i = 0; i < size(); i += 1) {
            if (_chars[i] == c) {
                return i;
            }
        }
        throw new EnigmaException("Character " + c + " not found.");
    }

    /** Returns true if C has already been added to CHARS. */
    boolean alreadyAdded(char c) {
        for (char x :_chars) {
            if (x == c) {
                return true;
            }
        }
        return false;
    }

    /** Returns String of all characters in alphabet. */
    String string() {
        return _charsString;
    }

    /** Array containing characters of this alphabet. */
    private char[] _chars;

    /** String containing the characters of this alphabet. */
    private String _charsString;
}
