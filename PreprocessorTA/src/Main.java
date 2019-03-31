
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
        String foaf = "http://xmlns.com/foaf/0.1/";
        String UPLOAD_FUSEKI = "http://localhost:3030/"+dbJenaFuseki;
        String READ_FUSEKI = "http://localhost:3030/"+dbJenaFuseki;
        String OWL_FILE_LOCATION = "D:/The-Tree-of-Heroes/family-ontology-r-stevens.owl";
        File fileRDF = new File("D:\\The-Tree-of-Heroes\\PreprocessorTA\\result.rdf");

        BasicConfigurator.configure(new NullAppender());
        System.out.println("Apache Jena Modelling, Reasoning and Inferring Tool");

                                 //MEMODELKAN FILE ACTOR DARI OWL ONTOLOGI FAMILY dan JENA-FUSEKI
        FileManager.get().addLocatorClassLoader(Main.class.getClassLoader());
        Model Instances = FileManager.get().loadModel(READ_FUSEKI);
        Instances.read(READ_FUSEKI,"RDF/XML");

        Model famonto = FileManager.get().loadModel(OWL_FILE_LOCATION);

                                //MERGING MODEL DARI JENA-FUSEKI DAN MODEL ONTOLOGI FAMILY
        final Model union = Instances;

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
        "Zainal_Mustafa"
                //Tokoh kerajaan

        };

        for (Integer counter = 0; counter < actors.length; counter++) {
            Model modelActor = fManager.loadModel("http://id.dbpedia.org/data/" + actors[counter] + ".rdf");
            final Resource actorResource = modelActor.getResource(res + actors[counter]);
            final Property hasSpouse = modelActor.getProperty(prop + "spouse");
            final Property hasChildren = modelActor.getProperty(prop + "children");

            StmtIterator stmtIteratorChild, stmtIteratorSpouse;
            stmtIteratorSpouse = modelActor.listStatements(actorResource, hasSpouse, (RDFNode) null);
            while (stmtIteratorSpouse.hasNext()) {
                Statement spouse = stmtIteratorSpouse.nextStatement();
                System.out.println("ACTOR SPOUSE" + spouse);
                union.add(spouse);
            }
            stmtIteratorChild = modelActor.listStatements(actorResource, hasChildren, (RDFNode) null);
            while (stmtIteratorChild.hasNext()) {
                Statement child = stmtIteratorChild.nextStatement();
                System.out.println("ACTOR CHILDREN" + child);
                union.add(child);
            }
        }

                                // REASONING MODEL UNION
        Reasoner pelletReasoner = PelletReasonerFactory.theInstance().create();
        InfModel reasonedModel = ModelFactory.createInfModel(pelletReasoner,union);

                                //QUERY TESTING
        String queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX dbr: <http://dbpedia.org/resource/>\n" +
                "PREFIX fam: <http://www.co-ode.org/roberts/family-tree.owl#>\n" +
                "SELECT ?s ?o\n" +
                "WHERE {\n" +
                "  ?s fam:hasParent ?o\n" +
                "}";

        Query query = QueryFactory.create(queryString);
        QueryExecution queryExecution = QueryExecutionFactory.create(query,reasonedModel);

        try {
            System.out.println("Start Query");
            ResultSet resultSet = queryExecution.execSelect();
            while (resultSet.hasNext() ) {
                QuerySolution querySolution = resultSet.nextSolution();
                Resource uri = querySolution.getResource("s");
                Resource p = querySolution.getResource("p");
                RDFNode object = querySolution.get("o");
                System.out.println(uri+"   "+p+"   "+object+".");
            }
        }
        finally {
            queryExecution.close();
            System.out.println("End Query");
        }


                                /*KONVERSI KE FILE .RDF*/
        if(fileRDF.delete())
        {
            System.out.println("The old result.rdf file deleted successfully");
        }
        else System.out.println("Creating new result as RDF File");

        PrintStream fileStream = new PrintStream("result.rdf");
        System.setOut(fileStream);

        reasonedModel.write( System.out, "RDF/XML" );

                            // READ FILE LOCATION
        try (FileInputStream in = new FileInputStream(fileRDF)) {
            reasonedModel.read(in, null, "RDF/XML");
        } catch (IOException e) {
            e.printStackTrace();
        }
                            // UPLOAD TO JENA FUSEKI
        DatasetAccessor accessor = DatasetAccessorFactory
                .createHTTP(UPLOAD_FUSEKI);
        accessor.putModel(reasonedModel);
    }
}