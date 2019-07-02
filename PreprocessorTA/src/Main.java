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

        String dbJenaFuseki="brits";
        String READ_FUSEKI = "http://localhost:3030/"+dbJenaFuseki;
        String OWL_FILE_LOCATION = "D:/The-Tree-of-Heroes/ontologi_lokal.owl";
        File fileRDF = new File("D:\\The-Tree-of-Heroes\\PreprocessorTA\\result.rdf");

        BasicConfigurator.configure(new NullAppender());
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
                //Indonesian Emperors
                "Raden_Wijaya",
                "Hayam_Wuruk",
                "Jayanegara",
                "Airlangga",
                "Tribhuwana_Wijayatunggadewi",
                "Ken_Arok",
                "Ken_Dedes",
                "Anusapati",
                "Tohjaya",
                "Wisnuwardhana",
                "Kertanagara",
                "Prajnaparamita",
                "Jayanagara",
                "Wikramawardhana",
                "Suhita",
                "Kertawijaya",
                "Rajasawardhana",
                "Girishawardhana",
                "Suraprabhawa",
                "Girindrawardhana",
                "Raden_Patah",
                "Pati_Unus",
                "Trenggana",
                "Sunan_Prawoto",
                "Indreswari",
                "Narendraduhita",
                "Prajnaparamita",

                //Tokoh Sejarah
                "Soekarno",
                "Oetari",
                "Inggit_Garnasih",
                "Ratna_Sari_Dewi_Soekarno",
                "Haryati",
                "Hartini",
                "Kartini_Manoppo",
                "Yurike_Sanger",
                "Heldy_Djafar",
                "Fatmawati",
                "Megawati_Soekarnoputri",
                "Taufiq_Kiemas",
                "Puan_Maharani",
                "Guruh_Soekarnoputra",
                "Guntur_Soekarnoputra",
                "Kartika_Sari_Dewi_Soekarno",
                "Sukmawati_Soekarnoputri",
                "Rachmawati_Soekarnoputri",
                "Soeharto",
                "Siti_Hartinah",
                "Siti_Hardijanti_Rukmana",
                "Indra_Rukmana",
                "Hutomo_Mandala_Putra",
                "Siti_Hediati_Hariyadi",
                "Prabowo_Subianto",
                "Bambang_Trihatmodjo",
                "Sigit_Harjojudanto",
                "Siti_Hutami_Endang_Adiningsih",
                "Bacharuddin_Jusuf_Habibie",
                "Hasri_Ainun_Habibie",
                "Ilham_Akbar",
                "Thareq_Kemal",
                "Abdurrahman_Wahid",
                "Yenny_Wahid",
                "Inayah_Wulandari",
                "Sinta_Nuriyah",
                "Susilo_Bambang_Yudhoyono",
                "Kristiani_Herrawati",
                "Agus_Harimurti_Yudhoyono",
                "Annisa_Pohan",
                "Edhie_Baskoro_Yudhoyono",
                "Siti_Ruby_Aliya_Radjasa",
                "Jusuf_Kalla",
                "Mufidah_Jusuf_Kalla",
                "Solihin_Kalla",
                "Joko_Widodo",
//                "Iriana_Joko_Widodo",

                //British Royal Family
//                "Elizabeth_II",
//                "Prince_Philip,_Duke_of_Edinburgh",
//                "Anne,_Princess_Royal",
//                "Charles,_Prince_of_Wales",
//                "Prince_Edward,_Earl_of_Wessex",
//                "Prince_Andrew,_Duke_of_York",
//                "Diana,_Princess_of_Wales",
//                "Camilla,_Duchess_of_Cornwall",
//                "Mark_Phillips",
//                "Timothy_Laurence",
//                "Sarah,_Duchess_of_York",
//                "Sophie,_Countess_of_Wessex",
//                "Prince_William,_Duke_of_Cambridge",
//                "Catherine,_Duchess_of_Cambridge",
//                "Prince_Harry",
//                "Meghan_Markle",
//                "Peter_Phillips",
//                "Autumn_Phillips",
//                "Zara_Phillips",
//                "Mike_Tindall",
//                "Princess_Beatrice_of_York",
//                "Princess_Eugenie_of_York",
//                "Lady_Louise_Windsor",
//                "James,_Viscount_Severn",
//                "Prince_George_of_Cambridge",
//                "Princess_Charlotte_of_Cambridge",
//                "Savannah_Phillips",
//                "Isla_Phillips"
        };
        for (Integer counter = 0; counter < actors.length; counter++) {
            Model modelActor = fManager.loadModel("http://"+"id."+"dbpedia.org/data/" + actors[counter]);
            Instances.add(modelActor);
            System.out.println(actors[counter]);
        }

        // MERGING MODEL DARI JENA-FUSEKI DAN MODEL ONTOLOGI FAMILY
        final Model union = ModelFactory.createUnion(Instances,famonto);

        // REASONING MODEL UNION
        Reasoner pelletReasoner = PelletReasonerFactory.theInstance().create();
        InfModel reasonedModel = ModelFactory.createInfModel(pelletReasoner,union);
//        Model reasonedModel = union;


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
    }
}