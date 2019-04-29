import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.util.FileManager;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.varia.NullAppender;
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

        String dbJenaFuseki="famtree";
        String prop = "http://id.dbpedia.org/property/";
        String res = "http://id.dbpedia.org/resource/";
        String owl = "http://dbpedia.org/ontology/";
        String rdfs ="http://www.w3.org/1999/02/22-rdf-syntax-ns#";
        String schema="http://schema.org/";
        String foaf = "http://xmlns.com/foaf/0.1/";
        String UPLOAD_FUSEKI = "http://localhost:3030/"+dbJenaFuseki;
        String READ_FUSEKI = "http://localhost:3030/"+dbJenaFuseki;
        String OWL_FILE_LOCATION = "D:/The-Tree-of-Heroes/family-ontology.owl";
        File fileRDF = new File("D:\\The-Tree-of-Heroes\\PreprocessorTA\\result.rdf");

        BasicConfigurator.configure(new NullAppender());
        System.out.println("Apache Jena Modelling, Reasoning and Inferring Tool");

        //MEMODELKAN FILE ACTOR DARI OWL ONTOLOGI FAMILY dan JENA-FUSEKI
        FileManager.get().addLocatorClassLoader(Main.class.getClassLoader());
        Model Instances = FileManager.get().loadModel(READ_FUSEKI);
        Instances.read(READ_FUSEKI,"TTL");

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
                "Oemar_Said_Tjokroaminoto",
                "Ernest_Douwes_Dekker",
                "Dewi_Sartika",
                "Cut_Nyak_Dhien",
                "Diponegoro",
                "Fatmawati",
                "Ferdinand_Lumbantobing",
                "Frans_Kaisiepo",
                "Gatot_Soebroto",
                "Halim_Perdanakusuma",
                "Hamengkubuwana_I",
                "Hamengkubuwana_IX",
                "Hasjim_Asy%27ari",
                "Sultan_Hasanuddin",
                "Tuanku_Imam_Bonjol",
                "Iskandar_Muda",
                "Ismail_Marzuki",
                "Iswahjoedi",
                "Radin_Inten_II",
                "Johannes_Abraham_Dimara",
                "Djoeanda_Kartawidjaja",
                "Karel_Satsuit_Tubun",
                "Kartini",
                "Kasman_Singodimedjo",
                "I_Gusti_Ketut_Jelantik",
                "Ki_Hadjar_Dewantara",
                "Sultan_Mahmud_Badaruddin_II",
                "Malahayati",
                "Maria_Walanda_Maramis",
                "Martha_Christina_Tiahahu",
                "Mas_Tirtodarmo_Haryono",
                "Cut_Nyak_Meutia",
                "Mohammad_Husni_Thamrin",
                "Mohammad_Natsir",
                "Teuku_Muhammad_Hasan",
                "Mohammad_Yamin",
                "Muhammad_Yasin",
                "Muhammad_Zainuddin_Abdul_Madjid",
                "Moestopo",
                "Nani_Wartabone",
                "I_Gusti_Ngurah_Rai",
                "Nuku_Muhammad_Amiruddin",
                "Oto_Iskandar_di_Nata",
                "Pakubuwana_VI",
                "Pakubuwana_X",
                "Pattimura",
                "Pierre_Tendean",
                "Raja_Haji_Fisabilillah",
                "Rasuna_Said",
                "Radjiman_Wedyodiningrat",
                "Sam_Ratulangi",
                "Samanhudi",
                "Sisingamangaraja_XII",
                "Siswondo_Parman",
                "Slamet_Rijadi",
                "Soedirman",
                "Albertus_Soegijapranata",
                "Suharso",
                "Soekarni",
                "Sultan_Agung_dari_Mataram",
                "Andi_Sultan_Daeng_Radja",
                "Soepeno",
                "R._Suprapto_(pahlawan_revolusi)",
                "Soeprijadi",
                "Soeroso",
                "Ario_Soerjo",
                "Sutan_Syahrir",
                "Soetomo",
                "Sutomo",
                "Sutoyo_Siswomiharjo",
                "Syafruddin_Prawiranegara",
                "Syam%27un",
                "Syarif_Kasim_II_dari_Siak",
                "T.B._Simatupang",
                "Tan_Malaka",
                "Tirto_Adhi_Soerjo",
                "Teuku_Umar",
                "Untung_Suropati",
                "Oerip_Soemohardjo",
                "Wage_Rudolf_Soepratman",
                "Wahid_Hasjim",
                "Wahidin_Soedirohoesodo",
                "Wilhelmus_Zakaria_Johannes",
                "Yos_Sudarso",
                "Zainul_Arifin",
                "Zainal_Mustafa",
                //Tokoh kerajaan
                "Sultan_Agung_dari_Mataram",
                "Raden_Wijaya",
                "Hayam_Wuruk",
        };

        //MERGING MODEL DARI JENA-FUSEKI DAN MODEL ONTOLOGI FAMILY
        final Model union = ModelFactory.createUnion(Instances,famonto);

        for (String actor : actors) {
            Model modelActor = fManager.loadModel("http://id.dbpedia.org/data/" + actor + ".rdf");
            final Resource actorResource = modelActor.getResource(res + actor);
            final Property hasSpouse = modelActor.getProperty(prop + "spouse");
            final Property hasChildren = modelActor.getProperty(prop + "children");
            final Property hasName = modelActor.getProperty(prop + "name");
            final Property hasSibling = modelActor.getProperty(prop + "sibling");
            final Property hasParent = modelActor.getProperty(prop + "parent");
            final Property hasFather = modelActor.getProperty(prop + "father");
            final Property hasMother = modelActor.getProperty(prop + "mother");
            final Property hasType = modelActor.getProperty(rdfs + "type");
            final Property hasThumbnail = modelActor.getProperty(owl + "thumbnail");

            StmtIterator stmtIterator;
            stmtIterator = modelActor.listStatements(actorResource, hasSpouse, (RDFNode) null);
            while (stmtIterator.hasNext()) {
                Statement spouse = stmtIterator.nextStatement();
                System.out.println("ACTOR SPOUSE" + spouse);
                union.add(spouse);
            }
            stmtIterator = modelActor.listStatements(actorResource, hasChildren, (RDFNode) null);
            while (stmtIterator.hasNext()) {
                Statement child = stmtIterator.nextStatement();
                System.out.println("ACTOR CHILDREN" + child);
                union.add(child);
            }

        }

        //REASONING MODEL UNION - bugs
        System.out.println("Reasoning..");
        Reasoner pelletReasoner = PelletReasonerFactory.theInstance().create();
        InfModel reasonedModel = ModelFactory.createInfModel(pelletReasoner,union);
        System.out.println("Reasoning done");

        //Model reasonedModel=union;
                                /*KONVERSI KE FILE .RDF*/
//        if(fileRDF.delete())
//        {
//            System.out.println("The old result.rdf file deleted successfully");
//        }
//        else System.out.println("Creating new result as RDF File");
//
//        PrintStream fileStream = new PrintStream("result.rdf");
//        System.setOut(fileStream);


        // READ FILE LOCATION
//        try (FileInputStream in = new FileInputStream(fileRDF)) {
//            reasonedModel.read(in, null, "RDF/XML");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        // UPLOAD TO JENA FUSEKI
        DatasetAccessor accessor = DatasetAccessorFactory
                .createHTTP(UPLOAD_FUSEKI);
        accessor.putModel(reasonedModel);
    }
}