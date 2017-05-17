package enigma;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Chris Jereza
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        for (int i = 0; i < notches.length(); i += 1) {
            if (!perm.alphabet().contains(notches.charAt(i))) {
                throw new EnigmaException("Incorrect format for NOTCHES.");
            }
        }
        _notches = notches;
    }

    /** Turn rotor once. */
    @Override
    void advance() {
        set(setting() + 1);
    }

    /** Returns true if rotor is at notch and should rotate. */
    boolean atNotch() {
        return (notches().indexOf(alphabet().toChar(setting())) != -1);
    }

    /** Returns NOTCHES. */
    String notches() {
        return _notches;
    }

    /** Returns true because this rotor is an instance of MovingRotor. */
    boolean rotates() {
        return true;
    }

    /** String containing this rotor's notches. */
    private String _notches;
}
