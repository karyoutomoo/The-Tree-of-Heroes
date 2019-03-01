import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
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

                                 // MEMODELKAN FILE ACTOR DARI OWL ONTOLOGI FAMILY dan JENA-FUSEKI

        FileManager.get().addLocatorClassLoader(Main.class.getClassLoader());
        Model Instances = FileManager.get().loadModel("http://localhost:3030/pohonkeluarga");
        Model famonto = FileManager.get().loadModel("D:/The-Tree-of-Heroes/famonto.owl");


                                // MERGING FILE ACTOR DARI JENA-FUSEKI DAN ONTOLOGI FAMILY

        final Model union = ModelFactory.createUnion(Instances,famonto);

                                // REASONING

                                // EKSTRAKSI

        String queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX fam: <http://www.semanticweb.org/asus/ontologies/2019/1/untitled-ontology-41#>\n" +
                "SELECT ?s ?o\n" +
                "WHERE {\n" +
                "  ?s fam:hasName ?o\n" +
                "}";

        Query query = QueryFactory.create(queryString);
        QueryExecution queryExecution = QueryExecutionFactory.create(query,Instances);
        try {
            System.out.println("Start Execution from OWL file");
            ResultSet resultSet = queryExecution.execSelect();
            while (resultSet.hasNext() ) {
                QuerySolution querySolution = resultSet.nextSolution();
                Resource uri = querySolution.getResource("s");
                Resource p = querySolution.getResource("p");
                RDFNode object = querySolution.get("o");
                System.out.println(uri+"     "+object);
            }
        }
        finally {
            queryExecution.close();
            System.out.println("End Execution from OWL file");
        }
        // KONVERSI
    }
}
