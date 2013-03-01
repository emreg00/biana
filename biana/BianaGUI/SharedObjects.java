import javax.swing.JFileChooser;
import java.io.File;
import javax.swing.filechooser.*;


public abstract class SharedObjects {

    public static final String TAB_SEPARATED_DESCRIPTION = "TAB SEPARATED - All data";
    public static final String FASTA_PROTEIN_SEQUENCE_DESCRIPTION = "FASTA - Amino Acid Sequences";
    public static final String FASTA_NUCLEOTIDE_SEQUENCE_DESCRIPTION = "FASTA - Nucleotide Sequences";

    public static JFileChooser fileChooser = new JFileChooser();

    public static class ProteinSequenceFilter extends FileFilter {
        public boolean accept(File f) {
            //if (f.isDirectory()) {
                return true;
            //}
        }
        public String getDescription() {
            return FASTA_PROTEIN_SEQUENCE_DESCRIPTION; 
        }
    }

    public static class NucleotideSequenceFilter extends FileFilter {
        public boolean accept(File f) {
            return true;
        }
        public String getDescription() {
            return  FASTA_NUCLEOTIDE_SEQUENCE_DESCRIPTION; 
        }
    }

    public static class TabulatedFilter extends FileFilter {
        public boolean accept(File f) {
            return true;
        }
        public String getDescription() {
            return TAB_SEPARATED_DESCRIPTION;
        }
    }

    public static TabulatedFilter filterTab = new TabulatedFilter();
    public static ProteinSequenceFilter filterPro = new ProteinSequenceFilter();
    public static NucleotideSequenceFilter filterNuc = new NucleotideSequenceFilter();


}

