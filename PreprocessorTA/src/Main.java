import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.rdf.model.impl.StmtIteratorImpl;
import com.hp.hpl.jena.reasoner.Reasoner;
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

        String UPLOAD_FUSEKI = "http://localhost:3030/pohonkeluarga";
        String READ_FUSEKI = "http://localhost:3030/pohonkeluarga";
        String OWL_FILE_LOCATION = "D:/The-Tree-of-Heroes/family-ontology-r-stevens.owl";
        File fileRDF = new File("D:\\The-Tree-of-Heroes\\PreprocessorTA\\result.rdf");

        BasicConfigurator.configure(new NullAppender());
        final OntModel ontModel = ModelFactory.createOntologyModel( OntModelSpec.OWL_DL_MEM );
        System.out.println("Apache Jena Modelling, Reasoning and Inferring Tool");

                                 // MEMODELKAN FILE ACTOR DARI OWL ONTOLOGI FAMILY dan JENA-FUSEKI
        FileManager.get().addLocatorClassLoader(Main.class.getClassLoader());
        Model Instances = FileManager.get().loadModel(READ_FUSEKI);
        Instances.read(READ_FUSEKI,"TTL");

        Model famonto = FileManager.get().loadModel(OWL_FILE_LOCATION);

                                //ADD ACTOR
        FileManager fManager = FileManager.get();
        fManager.addLocatorURL();
        Model modelActor = fManager.loadModel("http://id.dbpedia.org/data/Megawati_Soekarnoputri.rdf");
        Instances.add(modelActor);

                                // MERGING MODEL DARI JENA-FUSEKI DAN MODEL ONTOLOGI FAMILY
        final Model union = ModelFactory.createUnion(Instances,famonto);

                                // REASONING MODEL UNION
        Reasoner reasoner = PelletReasonerFactory.theInstance().create();
        Model reasonedModel = ModelFactory.createInfModel(reasoner,union);

                                //QUERY TESTING
        String queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX dbr: <http://dbpedia.org/resource/>\n" +
                "PREFIX fam: <http://www.semanticweb.org/asus/ontologies/2019/1/untitled-ontology-41#>\n" +
                "SELECT ?s ?o\n" +
                "WHERE {\n" +
                "  ?s fam:hasParent ?o\n" +
                "}";

        Query query = QueryFactory.create(queryString);
        QueryExecution queryExecution = QueryExecutionFactory.create(query,reasonedModel);
        //untuk test tanpa reasoning
        //QueryExecution queryExecution = QueryExecutionFactory.create(query,union);

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

                                // EKSTRAKSI INFERRED MODEL ONLY
        InfModel pModel = ModelFactory.createInfModel( PelletReasonerFactory.theInstance().create(), reasonedModel);
        System.out.println("Getting Inferred Model..");
        ExtendedIterator<Statement> stmts = pModel.listStatements().filterDrop( new Filter<Statement>(){
            public boolean accept(Statement o){
                return union.contains(o);
            }
        });

                                // KONVERSI KE FILE .RDF
        Model deductions = ModelFactory.createDefaultModel().add( new StmtIteratorImpl( stmts ));

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

        deductions.write( System.out, "RDF/XML" );

                                // UPLOAD TO JENA FUSEKI
        // parse the file
        try (FileInputStream in = new FileInputStream(fileRDF)) {
            deductions.read(in, null, "RDF/XML");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // upload the resulting model
        DatasetAccessor accessor = DatasetAccessorFactory
                .createHTTP(UPLOAD_FUSEKI);
        accessor.putModel(deductions);
    }
}