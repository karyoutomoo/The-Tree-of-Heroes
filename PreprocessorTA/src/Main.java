import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileManager;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.varia.NullAppender;

public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        BasicConfigurator.configure(new NullAppender());
        System.out.println("Apache Jena Modelling, Reasoning and Inferring Tool");

        // MEMODELKAN FILE ACTOR DARI JENA-FUSEKI DAN ONTOLOGI FAMILY

        FileManager.get().addLocatorClassLoader(Main.class.getClassLoader());
        Model Instances = FileManager.get().loadModel("D:/The-Tree-of-Heroes/main.owl");
        Model famonto = FileManager.get().loadModel("D:/The-Tree-of-Heroes/famonto.owl");

        QueryExecution qe = QueryExecutionFactory.sparqlService(
                "http://localhost:3030/pohonkeluarga/query", "SELECT * WHERE {?s ?p ?o}");
        ResultSet results = qe.execSelect();
        System.out.println("Start Execution from JENA FUSEKI");
        ResultSetFormatter.out(System.out, results);
        System.out.println("End Execution from JENA FUSEKI");
        for(int i=0;i<200;i++){
            System.out.print("*");
            if(i==199){
                System.out.println("*");
                break;
            }
        }
        // MERGING FILE ACTOR DARI JENA-FUSEKI DAN ONTOLOGI FAMILY

        final Model union = ModelFactory.createUnion(Instances,famonto);

        // REASONING

        // EKSTRAKSI

        String queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX fam: <http://www.semanticweb.org/asus/ontologies/2019/1/untitled-ontology-41#>\n" +
                "SELECT ?x\n" +
                "WHERE {\n" +
                "  ?x fam:hasName ?y\n" +
                "}";

        Query query = QueryFactory.create(queryString);
        QueryExecution queryExecution = QueryExecutionFactory.create(query,union);
        try {
            System.out.println("Start Execution from OWL file");
            ResultSet resultSet = queryExecution.execSelect();
            while (resultSet.hasNext() ) {
                QuerySolution querySolution = resultSet.nextSolution();
                Resource uri = querySolution.getResource("x");
                System.out.println(uri);

            }
        }finally {
            queryExecution.close();
            System.out.println("End Execution from OWL file");
        }
        // KONVERSI
    }
}
