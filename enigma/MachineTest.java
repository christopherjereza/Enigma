package enigma;

import org.junit.Rule;
import org.junit.Test;
import java.util.ArrayList;
import org.junit.rules.Timeout;
import static enigma.TestUtils.UPPER;
import static org.junit.Assert.assertEquals;

/**
 * The suite of all JUnit tests for the Machine class.
 * @author Chris Jereza
 */
public class MachineTest {

    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    private Alphabet alpha = UPPER;

    String cycles1 = "(AELTPHQXRU) (BKNW) (CMOY) (DFG) (IV) (JZ) (S)";
    Permutation perm1 = new Permutation(cycles1, UPPER);
    MovingRotor rotor1 = new MovingRotor("I", perm1, "Q");

    String cycles2 = "(FIXVYOMW) (CDKLHUP) (ESZ) (BJ) (GR) (NT) (A) (Q)";
    Permutation perm2 = new Permutation(cycles2, UPPER);
    MovingRotor rotor2 = new MovingRotor("II", perm2, "E");

    String cycles3 = "(ABDHPEJT) (CFLVMZOYQIRWUKXSG) (N)";
    Permutation perm3 = new Permutation(cycles3, UPPER);
    MovingRotor rotor3 = new MovingRotor("III", perm3, "V");

    String cycles4 = "(AEPLIYWCOXMRFZBSTGJQNH) (DV) (KU)";
    Permutation perm4 = new Permutation(cycles4, UPPER);
    MovingRotor rotor4 = new MovingRotor("IV", perm4, "J");

    String cycles5 = "(AVOLDRWFIUQ)(BZKSMNHYC) (EGTJPX)";
    Permutation perm5 = new Permutation(cycles5, UPPER);
    MovingRotor rotor5 = new MovingRotor("V", perm5, "Z");

    String cycles6 = "(AJQDVLEOZWIYTS) (CGMNHFUX) (BPRK)";
    Permutation perm6 = new Permutation(cycles6, UPPER);
    MovingRotor rotor6 = new MovingRotor("VI", perm6, "ZM");

    String cycles7 = "(ANOUPFRIMBZTLWKSVEGCJYDHXQ)";
    Permutation perm7 = new Permutation(cycles7, UPPER);
    MovingRotor rotor7 = new MovingRotor("VII", perm7, "ZM");

    String cycles8 = "(AFLSETWUNDHOZVICQ) (BKJ) (GXY) (MPR)";
    Permutation perm8 = new Permutation(cycles8, UPPER);
    MovingRotor rotor8 = new MovingRotor("VIII", perm8, "ZM");

    String cyclesBeta = "(ALBEVFCYODJWUGNMQTZSKPR) (HIX)";
    Permutation permBeta = new Permutation(cyclesBeta, UPPER);
    FixedRotor rotorBeta = new FixedRotor("BETA", permBeta);

    String cyclesGamma = "(AFNIRLBSQWVXGUZDKMTPCOYJHE)";
    Permutation permGamma = new Permutation(cyclesGamma, UPPER);
    FixedRotor rotorGamma = new FixedRotor("GAMMA", permGamma);

    String cyclesB = "(AE) (BN) (CK) (DQ) (FU) (GY) (HW) (IJ)"
                    + " (LO) (MP) (RX) (SZ) (TV)";
    Permutation permB = new Permutation(cyclesB, UPPER);
    Reflector b = new Reflector("B", permB);

    String cyclesC = "(AR) (BD) (CO) (EJ) (FN) (GT) (HK) "
                    + "(IV) (LM) (PW) (QZ) (SX) (UY)";
    Permutation permC = new Permutation(cyclesC, UPPER);
    Reflector c = new Reflector("C", permC);

    ArrayList<Rotor> allRotors = new ArrayList<>();
    Machine testMachine1;

    private void createTestMachine1() {
        allRotors.add(rotor1);
        allRotors.add(rotor2);
        allRotors.add(rotor3);
        allRotors.add(rotor4);
        allRotors.add(rotor5);
        allRotors.add(rotor6);
        allRotors.add(rotor7);
        allRotors.add(rotor8);
        allRotors.add(rotorBeta);
        allRotors.add(rotorGamma);
        allRotors.add(b);
        allRotors.add(c);
        testMachine1 = new Machine(alpha, 5, 3, allRotors);
        String[] rotorNames = {"B", "BETA", "III", "IV", "I"};
        testMachine1.insertRotors(rotorNames);
        testMachine1.setRotors("AXLE");
        testMachine1.setPlugboard(new Permutation("(Y F)(ZH) (A) (B) (C)"
                + "(D)(E)(G)(I)(J)(K)(L)(M)(N)(O)"
                + "(P)(Q)(R)(S)(T)(U)(V)(W)(X)", alpha));
    }

    @Test
    public void testMachine1constructor() {
        createTestMachine1();
        assertEquals(5, testMachine1.numRotors());
        assertEquals(3, testMachine1.numPawls());
        assertEquals(alpha, testMachine1.alphabet());
    }

    @Test
    public void testInsert1() {
        createTestMachine1();
        assertEquals("I", testMachine1.getRotor("I").name());
    }

    @Test
    public void checkSettings() {
        createTestMachine1();
        assertEquals(0, testMachine1.getRotor("BETA").setting());
        assertEquals(alpha.toInt('X'), testMachine1.getRotor("III").setting());
        assertEquals(alpha.toInt('L'), testMachine1.getRotor("IV").setting());
        assertEquals(alpha.toInt('E'), testMachine1.getRotor("I").setting());
    }

    @Test
    public void checkPlugboard() {
        createTestMachine1();
        assertEquals('F', testMachine1.plugboard().permute('Y'));
        assertEquals('Z', testMachine1.plugboard().permute('H'));
        assertEquals('Y', testMachine1.plugboard().permute('F'));
        assertEquals('H', testMachine1.plugboard().permute('Z'));
    }

    @Test
    public void checkCharConversion() {
        createTestMachine1();
        assertEquals('Z', testMachine1.convert('Y'));
    }

    @Test
    public void checkStringConversion() {
        createTestMachine1();
        testMachine1.setPlugboard(new Permutation("(HQ) (EX)"
                + " (IP) (TR) (BY)", alpha));
        assertEquals("QVPQSOKOILPUBKJZPISFXDW",
                    testMachine1.convert("FROM HIS SHOULDER HIAWATHA"));
        assertEquals("BHCNSCXNUOAATZXSRCFYDGU",
                    testMachine1.convert("TOOK THE CAMERA OF ROSEWOOD"));
    }

    @Test
    public void checkPostSettings() {
        createTestMachine1();
        for (int i = 0; i < 12; i += 1) {
            char x = testMachine1.convert('Y');
        }
        assertEquals(testMachine1.settings(), "AXLQ");
    }
}
