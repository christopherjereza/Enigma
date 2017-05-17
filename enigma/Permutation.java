package enigma;
import java.util.ArrayList;
import java.util.HashMap;
import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Chris Jereza
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters not
     *  included in any cycle map to themselves. Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _cyclesString = cycles;
        _alphabet = alphabet;
        _size = alphabet().size();
        _cycles = new HashMap<>();
        if (cycles.equals("")) {
            _cycles = null;
        } else {
            setCycles(cycles);
        }
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        for (int i = 0; i < cycle.length() - 1; i += 1) {
            String value = Character.toString(cycle.charAt(i + 1));
            _cycles.put(Character.toString(cycle.charAt(i)), value);
        }
        String last = Character.toString(cycle.charAt(cycle.length() - 1));
        String first = Character.toString(cycle.charAt(0));
        _cycles.put(last, first);
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Separate CYCLES string into arrays and store in cycles[][]. */
    private void setCycles(String cycles) {
        String last = null;
        int size = 0;
        ArrayList<String> currentCycle = new ArrayList<>();
        for (int i = 0; i < cycles.length(); i += 1) {
            if (cycles.charAt(i) == '(') {
                currentCycle = new ArrayList<>();
            } else if (cycles.charAt(i) == ')') {
                for (int k = 0; k < currentCycle.size() - 1; k += 1) {
                    if (_cycles.containsKey(currentCycle.get(k))) {
                        throw new EnigmaException("Cannot have duplicates.");
                    }
                    _cycles.put(currentCycle.get(k), currentCycle.get(k + 1));
                }
                if (_cycles.containsKey(last)) {
                    throw new EnigmaException("Cannot have duplicates");
                }
                _cycles.put(last, currentCycle.get(0));
            } else if (cycles.charAt(i) != ' ') {
                last =  Character.toString(cycles.charAt(i));
                currentCycle.add(last);
                size += 1;
            }
        }
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _size;
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        return wrap(alphabet().toInt(permute(_alphabet.toChar(p))));
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        if (_cycles == null) {
            return c;
        }
        int index = c % alphabet().size();
        for (String key : _cycles.keySet()) {
            if (alphabet().toChar(index) == _cycles.get(key).charAt(0)) {
                return wrap(_alphabet.toInt(key.charAt(0)));
            }
        }
        return c;
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        if (_cycles == null) {
            return p;
        }
        if (_cycles.containsKey(Character.toString(p))) {
            return _cycles.get(Character.toString(p)).charAt(0);
        } else {
            return p;
        }
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        return alphabet().toChar(invert(alphabet().toInt(c)));
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (String key : _cycles.keySet()) {
            if (key.equals(_cycles.get(key))) {
                return false;
            }
        }
        return true;
    }

    /** Return Mapping of CYCLES. */
    HashMap<String, String> cycles() {
        return _cycles;
    }

    /** Return String representation of CYCLES. */
    String cyclesString() {
        return _cyclesString;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** Size of this permutation. */
    private int _size;

    /** HashMap containing cycles (mappings). */
    private HashMap<String, String> _cycles;

    /** String representing cycles. */
    private String _cyclesString;
}

