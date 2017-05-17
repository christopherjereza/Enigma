package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Chris Jereza
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);
        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        _machine = readConfig();
        boolean set = false;
        String next;
        while (_input.hasNextLine()) {
            next = _input.nextLine();
            if (next.indexOf('*') != -1) {
                setUp(_machine, next);
                set = true;
            } else if (set) {
                printMessageLine(next);
            } else {
                throw new EnigmaException("Input must begin with Setting.");
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            checkNext();
            String alphabet = _config.next();
            _alphabet = new Alphabet(alphabet);
            checkNextInt();
            int numRotors = _config.nextInt();
            checkNextInt();
            int numPawls = _config.nextInt();
            ArrayList<Rotor> rotors = new ArrayList<>();
            checkNext();
            String next = _config.next();
            while (_config.hasNext()) {
                String rotorName = next.toUpperCase();
                checkNext();
                next = _config.next();
                char rotorType = next.charAt(0);
                String notches = "";
                for (int i = 1; i < next.length(); i += 1) {
                    notches += next.charAt(i);
                }
                String cycles = "";
                if (_config.hasNext()) {
                    next = _config.next();
                }
                while (next.indexOf('(') != -1 && _config.hasNext()) {
                    if (next.indexOf(')') == -1) {
                        throw new EnigmaException("Improper parentheses");
                    }
                    cycles += next;
                    next = _config.next();
                }
                if (!_config.hasNext()) {
                    cycles += next;
                }
                Permutation perm = new Permutation(cycles, _alphabet);
                if (rotorType == 'M') {
                    rotors.add(new MovingRotor(rotorName, perm, notches));
                } else if (rotorType == 'N') {
                    rotors.add(new FixedRotor(rotorName, perm));
                } else {
                    rotors.add(new Reflector(rotorName, perm));
                }
            }
            return new Machine(_alphabet, numRotors, numPawls, rotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Throws exception if CONFIG.hasNext is false. */
    private void checkNext() {
        if (!_config.hasNext()) {
            throw new EnigmaException("Config file has wrong format.");
        }
    }

    /**Throws exception if CONFIG.hasNextInt() is false. */
    private void checkNextInt() {
        if (!_config.hasNextInt()) {
            throw new EnigmaException("Config file has wrong format.");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            return null;
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        settings = settings.replaceAll("\\s+", " ");
        String[] setArray = settings.split(" ");
        int numRotors = 0;
        for (int i = 1; i < setArray.length; i += 1) {
            for (Rotor x : M.getAllRotors()) {
                if (x.name().equals(setArray[i])) {
                    numRotors += 1;
                }
            }
        }
        String[] rotors = new String[numRotors];
        System.arraycopy(setArray, 1, rotors, 0, numRotors);
        M.insertRotors(rotors);
        M.setRotors(setArray[numRotors + 1]);
        String plugboardCycles = "";
        for (int x = numRotors + 2; x < setArray.length; x += 1) {
            plugboardCycles += setArray[x];
        }
        M.setPlugboard(new Permutation(plugboardCycles, _alphabet));
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        msg = _machine.convert(msg);
        String word = "";
        for (int i = 0; i < msg.length(); i += 1) {
            word += msg.charAt(i);
            if (word.length() % 5 == 0 || i == msg.length() - 1) {
                _output.print(word + " ");
                word = "";
            }
        }
        _output.println();
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** String containing settings line for Machine. */
    private String _settings;

    /** Machine to be used. */
    private Machine _machine;
}
