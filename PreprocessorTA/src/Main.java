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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);
    public static void main(String[] args) throws FileNotFoundException {

        String UPLOAD_FUSEKI = "http://localhost:3030/pohonkeluarga/upload";
        String READ_FUSEKI = "http://localhost:3030/pohonkeluarga/data";
        String OWL_FILE_LOCATION = "D:/The-Tree-of-Heroes/famonto.owl";

        BasicConfigurator.configure(new NullAppender());
        final OntModel ontModel = ModelFactory.createOntologyModel( OntModelSpec.OWL_DL_MEM );
        System.out.println("Apache Jena Modelling, Reasoning and Inferring Tool");

                                 // MEMODELKAN FILE ACTOR DARI OWL ONTOLOGI FAMILY dan JENA-FUSEKI
        FileManager.get().addLocatorClassLoader(Main.class.getClassLoader());
        //Model Instances = FileManager.get().loadModel(READ_FUSEKI);
        Model Instances = ModelFactory.createDefaultModel();
        Instances.read(READ_FUSEKI,"TTL");

        Model famonto = FileManager.get().loadModel(OWL_FILE_LOCATION);


                                // MERGING MODEL DARI JENA-FUSEKI DAN MODEL ONTOLOGI FAMILY
        final Model union = ModelFactory.createUnion(Instances,famonto);

                                // REASONING DAN INFERRING MODEL UNION
        Reasoner reasoner = PelletReasonerFactory.theInstance().create();
        InfModel infModel = ModelFactory.createInfModel(reasoner,union);

                                // QUERY TESTING
        String queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX fam: <http://www.semanticweb.org/asus/ontologies/2019/1/untitled-ontology-41#>\n" +
                "SELECT ?s ?o\n" +
                "WHERE {\n" +
                "  ?s fam:hasParent ?o\n" +
                "}";

        Query query = QueryFactory.create(queryString);
        QueryExecution queryExecution = QueryExecutionFactory.create(query,infModel);
        try {
            System.out.println("Start Execution from OWL file");
            ResultSet resultSet = queryExecution.execSelect();
            while (resultSet.hasNext() ) {
                QuerySolution querySolution = resultSet.nextSolution();
                Resource uri = querySolution.getResource("s");
                Resource p = querySolution.getResource("p");
                RDFNode object = querySolution.get("o");
                System.out.println(uri+"   "+p+"   "+object);
            }
        }
        finally {
            queryExecution.close();
            System.out.println("End Execution from OWL file");
        }

                                // EKSTRAKSI INFERRED MODEL ONLY
        System.out.println("Getting Inferred Model..");
        ExtendedIterator<Statement> stmts = infModel.listStatements().filterDrop( new Filter<Statement>(){
            public boolean accept(Statement o){
                return ontModel.contains(o);
            }
        });

                                // KONVERSI KE .RDF
        Model deductions = ModelFactory.createDefaultModel().add( new StmtIteratorImpl( stmts ));

        System.out.println("Creating RDF File..");
        PrintStream fileStream = new PrintStream("result.rdf");
        System.setOut(fileStream);

        deductions.write( System.out, "RDF/XML" );

                                // UPLOAD TO JENA FUSEKI
        System.out.println("Uploading to Jena Fuseki Server..");
    }
}
