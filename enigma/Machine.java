package enigma;

import java.util.Collection;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Chris Jereza
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        if (pawls > numRotors - 1) {
            throw new EnigmaException("Invalid number of pawls.");
        }
        _alphabet = alpha;
        _numRotors = numRotors;

        _pawls = pawls;
        _allRotors = allRotors;
        _rotors = new Rotor[numRotors];
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        if (rotors.length != _rotors.length) {
            throw new EnigmaException("Incorrect number of rotors.");
        }
        for (int i = 0; i < rotors.length; i += 1) {
            Rotor rotorToInsert = getRotor(rotors[i]);
            for (int x = 0; x < i; x += 1) {
                if (rotors()[x].name().equals(rotorToInsert.name())) {
                    throw new EnigmaException("Cannot insert duplicate rotor.");
                }
            }
            if (i == 0) {
                if (!rotorToInsert.reflecting()) {
                    throw new EnigmaException("Reflector must be in 0 slot.");
                }
            } else if (i < rotors.length - _pawls) {
                if (rotorToInsert.rotates()) {
                    throw new EnigmaException("Moving Rotor must have a pawl.");
                }
            } else if (i >= rotors.length - _pawls) {
                if (!rotorToInsert.rotates()) {
                    throw new EnigmaException("Fixed Rotor cannot have pawl.");
                }
            }
            _rotors[i] = getRotor(rotors[i]);
        }
    }

    /** Set my rotors according to SETTING, which must be a string of four
     *  upper-case letters. The first letter refers to the leftmost
     *  rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        for (int i = 0; i < setting.length(); i += 1) {
            if (!alphabet().contains(setting.charAt(i))) {
                throw new EnigmaException("Setting not in alphabet.");
            }
        }
        if (setting.length() != rotors().length - 1) {
            throw new EnigmaException("Incorrect number of settings.");
        }
        _settings = setting;
        for (int i = 1; i < _rotors.length; i += 1) {
            _rotors[i].set(setting.charAt(i - 1));
        }
    }

    /** Sets all rotors (excluding reflector) to original positions. */
    void resetRotors() {
        String setting = "";
        for (int i = 1; i < numRotors(); i += 1) {
            setting = setting + "A";
        }
        setRotors(setting);
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        int current = c;
        advanceMachine();
        String checkKey = Character.toString(alphabet().toChar(current));
        if (plugboard().cycles() != null) {
            if (plugboard().cycles().containsKey(checkKey)) {
                current = _plugboard.permute(current);
            }
        }
        for (int i = _rotors.length - 1; i >= 0; i -= 1) {
            current = _rotors[i].convertForward(current);
        }
        for (int i = 1; i < _rotors.length; i += 1) {
            current = _rotors[i].convertBackward(current);
        }
        checkKey = Character.toString(alphabet().toChar(current));
        if (plugboard().cycles() != null) {
            if (plugboard().cycles().containsValue(checkKey)) {
                current = _plugboard.invert(current);
            }
        }
        return current;
    }

    /** Returns the result of converting the input character C (as a
     * character in the alphabet), after first advancing the machine. */
    char convert(char c) {
        return alphabet().toChar(convert(alphabet().toInt(c)));
    }

    /** Advances the machine. */
    void advanceMachine() {
        boolean[] moveRotor = new boolean[_rotors.length];
        moveRotor[_rotors.length - 1] = true;
        for (int i = _rotors.length - 1; i > 0; i -= 1) {
            if (_rotors[i].atNotch()) {
                if (_rotors[i].rotates() && _rotors[i - 1].rotates()) {
                    moveRotor[i] = true;
                    moveRotor[i - 1] = true;
                }
            }
        }
        for (int i = 0; i < moveRotor.length; i += 1) {
            if (moveRotor[i]) {
                _rotors[i].advance();
            }
        }
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String newMsg = "";
        msg = msg.replaceAll("\\s+", "");
        for (int i = 0; i < msg.length(); i += 1) {
            newMsg = newMsg + convert(Character.toUpperCase(msg.charAt(i)));
        }
        return newMsg;
    }

    /** Returns Rotor object with name NAME. */
    Rotor getRotor(String name) {
        for (Rotor r : _allRotors) {
            if (r.name().equals(name)) {
                return r;
            }
        }
        throw new EnigmaException("Rotor " + name + " not found.");
    }

    /** Returns the settings of all rotors (excluding reflector) as a String. */
    String settings() {
        _settings = "";
        for (int i = 1; i < rotors().length; i += 1) {
            _settings += alphabet().toChar(rotors()[i].setting());
        }
        return _settings;
    }

    /** Return String containing names of all available Rotors. */
    String allRotors() {
        String str = "";
        for (Rotor x : _allRotors) {
            str = str + x.name() + " ";
        }
        return str;
    }

    /** Returns String of names of all rotors in this machine. */
    String rotorsString() {
        String result = "";
        for (Rotor r : rotors()) {
            result = result + r.name() + " ";
        }
        return result;
    }

    /** Returns Collection of all available Rotors. */
    Collection<Rotor> getAllRotors() {
        return _allRotors;
    }

    /** Return PLUGBOARD. */
    Permutation plugboard() {
        return _plugboard;
    }

    /** Return ALPHABET. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return ROTORS (array of rotors inserted into machine. */
    Rotor[] rotors() {
        return _rotors;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Number of rotors to be placed in machine (should be 5). */
    private int _numRotors;

    /** Number of pawls in machine. */
    private int _pawls;

    /** Collection of all available rotors. */
    private Collection<Rotor> _allRotors;

    /** Array of rotors currently placed in machine. */
    private Rotor[] _rotors;

    /** Permutation representing the plugboard. */
    private Permutation _plugboard;

    /** String representing settings of rotors (excluding the reflector). */
    private String _settings;
}
