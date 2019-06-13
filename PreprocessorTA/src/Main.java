import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.rdf.model.impl.StmtIteratorImpl;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerFactory;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.varia.NullAppender;
import com.hp.hpl.jena.util.iterator.Filter;
import org.mindswap.pellet.jena.PelletReasonerFactory;
import java.io.*;

/**
 * Simple Ontology and DBpedia learning service preprocessor for
 * family tree web app.
 *
 * @author FAIQ, karyoutomoo@Gmail.com
 */

public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);
    public static void main(String[] args) throws FileNotFoundException {

        String dbJenaFuseki="brits";
        String prop = "http://id.dbpedia.org/property/";
        String res = "http://id.dbpedia.org/resource/";
        String rdfs ="http://www.w3.org/1999/02/22-rdf-syntax-ns#";
        String schema="http://schema.org/";
        String foaf = "http://xmlns.com/foaf/0.1/";
        String UPLOAD_FUSEKI = "http://localhost:3030/"+dbJenaFuseki;
        String READ_FUSEKI = "http://localhost:3030/"+dbJenaFuseki;
        String OWL_FILE_LOCATION = "D:/The-Tree-of-Heroes/ontologi_lokal.owl";
        File fileRDF = new File("D:\\The-Tree-of-Heroes\\PreprocessorTA\\result.rdf");

        BasicConfigurator.configure(new NullAppender());
        final OntModel ontModel = ModelFactory.createOntologyModel( OntModelSpec.OWL_DL_MEM );
        System.out.println("Apache Jena Modelling, Reasoning and Inferring Tool");

        // MEMODELKAN FILE ACTOR DARI OWL ONTOLOGI FAMILY dan JENA-FUSEKI
        FileManager.get().addLocatorClassLoader(Main.class.getClassLoader());
        Model Instances = FileManager.get().loadModel(READ_FUSEKI);
        Instances.read(READ_FUSEKI,"RDF/XML");

        Model famonto = FileManager.get().loadModel(OWL_FILE_LOCATION);

        //ADD ACTOR
        FileManager fManager = FileManager.get();
        fManager.addLocatorURL();
        String[] actors = {
//      Pahlawan Nasional
                "Soekarno",
                "Mohammad_Hatta",
                "Abdul_Haris_Nasution",
                "Adam_Malik",
                "Ahmad_Yani",
                "Basuki_Rahmat",
                "Tjipto_Mangoenkoesoemo",
                "Diponegoro",
                "Fatmawati",
                "Teuku_Nyak_Arif",
                "Ferdinand_Lumbantobing",
                "Frans_Kaisiepo",
                "Halim_Perdanakusuma",
                "Hamengkubuwana_I",
                "Hamengkubuwana_IX",
                "Hasjim_Asy%27ari",
                "Sultan_Hasanuddin",
                "Tuanku_Imam_Bonjol",
                "Ki_Hadjar_Dewantara",
                "Cut_Nyak_Meutia",
                "Mohammad_Natsir",
                "I_Gusti_Ngurah_Rai",
                "Pattimura",
                "Slamet_Rijadi",
                "Soedirman",
                "Teuku_Umar",
                "Untung_Suropati",
                "Wahid_Hasjim",
                //Tokoh kerajaan
                "Raden_Wijaya",
                "Hayam_Wuruk",
                "Jayanegara",
                "Airlangga",
                "Tribhuwana_Wijayatunggadewi",
                "Ken_Arok",

        };

        String[] royalFamilies = {
                //British Royal Family
                "Elizabeth_II",
                "Prince_Philip,_Duke_of_Edinburgh",
                "Anne,_Princess_Royal",
                "Charles,_Prince_of_Wales",
                "Prince_Edward,_Earl_of_Wessex",
                "Prince_Andrew,_Duke_of_York",
                "Diana,_Princess_of_Wales",
                "Camilla,_Duchess_of_Cornwall",
                "Mark_Phillips",
                "Timothy_Laurence",
                "Sarah,_Duchess_of_York",
                "Sophie,_Countess_of_Wessex",
                "Prince_William,_Duke_of_Cambridge",
                "Catherine,_Duchess_of_Cambridge",
                "Prince_Harry",
                "Meghan_Markle",
                "Peter_Phillips",
                "Autumn_Phillips",
                "Zara_Phillips",
                "Mike_Tindall",
                "Princess_Beatrice_of_York",
                "Princess_Eugenie_of_York",
                "Lady_Louise_Windsor",
                "James,_Viscount_Severn",
                "Prince_George_of_Cambridge",
                "Princess_Charlotte_of_Cambridge"

        };

//        for (Integer counter = 0; counter < actors.length; counter++) {
//            Model modelActor = fManager.loadModel("http://id.dbpedia.org/data/" + actors[counter] + ".rdf");
//
//            Instances.add(modelActor);
//            System.out.println(actors[counter]);
//        }
        for (Integer counter = 0; counter < royalFamilies.length; counter++) {
            Model modelActor = fManager.loadModel("http://dbpedia.org/data/" + royalFamilies[counter] + ".ttl");

            Instances.add(modelActor);
            System.out.println(royalFamilies[counter]);
        }

        // MERGING MODEL DARI JENA-FUSEKI DAN MODEL ONTOLOGI FAMILY
        final Model union = ModelFactory.createUnion(Instances,famonto);

        // REASONING MODEL UNION
//        Reasoner reasoner = PelletReasonerFactory.theInstance().create();
//        InfModel reasonedModel = ModelFactory.createInfModel(reasoner,union);
        Model reasonedModel = union;

        // KONVERSI KE FILE .RDF

        if(fileRDF.delete())
        {
            System.out.println("The old result.rdf file deleted successfully");
        }
        else
        {
            System.out.println("Creating new result as RDF File");
        }
        PrintStream fileStream = new PrintStream("result.rdf");
        System.setOut(fileStream);

        reasonedModel.write( System.out, "RDF/XML" );

//        // UPLOAD TO JENA FUSEKI
//        // parse the file
//        try (FileInputStream in = new FileInputStream(fileRDF)) {
//            reasonedModel.read(in, null, "RDF/XML");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        // upload the resulting model
//        DatasetAccessor accessor = DatasetAccessorFactory
//                .createHTTP(UPLOAD_FUSEKI);
//        accessor.putModel(reasonedModel);
    }
}