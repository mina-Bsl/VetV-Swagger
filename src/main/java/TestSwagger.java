
import io.swagger.models.*;

import java.util.Map;
import java.util.Map.Entry;
import io.swagger.parser.SwaggerParser;
import org.hamcrest.CoreMatchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import io.swagger.models.parameters.Parameter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Random;
import org.json.simple.JSONObject;
import static junit.framework.TestCase.fail;


/**
 * Created by boussalia.
 */
public class TestSwagger {

    //The ErrorCollector rule allows execution of a test to continue after the first problem is found (for example, to collect _all_ the incorrect rows in a table, and report them all at once):
    @Rule
   public ErrorCollector collector = new ErrorCollector();


    // extract the properties
    @Test
    public void init (){
        Swagger swagger = new SwaggerParser().read("http://petstore.swagger.io/v2/swagger.json");

        String basePath = "http://petstore.swagger.io/#" ;

        final int NB = 5;


        for (Entry<String, Path> entry : swagger.getPaths().entrySet()) {
            String cle = entry.getKey();
            Path path = entry.getValue();

            Map<HttpMethod, Operation> listeOp = path.getOperationMap();

            System.out.print("\n-" + cle);

            for (Entry<HttpMethod, Operation> liste : listeOp.entrySet()) {
                HttpMethod methode = liste.getKey();
                Operation operation = liste.getValue();

                System.out.println("\nLa méthode " + methode + " de " + cle);


                System.out.println("-Paramètres: ");
                for (Parameter param : operation.getParameters()){

                    System.out.println("Name: " + param.getName());
                    System.out.println("Description: " + param.getDescription());
                    System.out.println("In: " + param.getIn());
                    System.out.println("Required: " + param.getRequired());

                }

                System.out.println("-Reponses:");
                Map<String , Response> listeResp = operation.getResponses();

                for (Entry<String, Response> resp : listeResp.entrySet()) {
                    String code = resp.getKey();
                    Response response = resp.getValue();

                    System.out.println(code + "\n Description: " + response.getDescription());
                }

                for (int i = 0; i < NB; i++) {

                    try {
                        Triplet<Integer, String> resultat = fuzzer(basePath + cle, methode.toString(), operation.getParameters());

                        int codeRetour = resultat.getFirst();
                        String msgRetour = resultat.getSecond();
                        String parametre = resultat.getThird();
                        System.out.println("code de retour ----------" + codeRetour);
                        System.out.println("msg de retour ----------" + msgRetour);
                        System.out.println("paramétre ----------" + parametre);

                        String currentResponse = "";
                        switch(codeRetour) {
                            case 405:
                                currentResponse = "Not allowed";
                                break;
                            default :
                                break;
                        }

                        collector.checkThat("\nTest de la méthode: " + methode + " "+ cle + "\n Le code de retour n'est pas bon. \n Le code retouné est:" + codeRetour+ "\n Le code attendu est: "+ listeResp.keySet()+"\nLe message: " + codeRetour + ": " + currentResponse, listeResp.containsKey(String.valueOf(codeRetour)),CoreMatchers.equalTo(true));

                    } catch (Exception e) {
                        e.printStackTrace();
                        fail("Erreur de connexion");
                    }
                }
            }

        }
    }
        // c'est la méthode qui fait les requetes et les envoie pour être comparer dans le main
    public static Triplet<Integer, String> fuzzer(String path, String method, List<Parameter> params) throws Exception {


        String parametres = "";

        JSONObject json = new JSONObject();

        for (Parameter param : params) {

                    int rdm = new Random().nextInt(7);
                    switch (rdm) {
                        case 0:
                            break;
                        case 1:
                            json.put("id", new Random().nextInt());
                            break;
                        case 2:
                            json.put("id", String.valueOf(new Random().nextInt()));
                            break;
                        case 3:
                            json.put("name", String.valueOf(new Random().nextInt()));
                            break;
                        case 4:
                            json.put("name", new Random().nextInt());
                            break;
                        case 5:
                            json.put("status", new Random().nextInt());
                            break;
                        case 6:
                            json.put("status", String.valueOf(new Random().nextInt()));
                            break;
                        default:
                            break;
                    };



        }

        //creation de la connexion

         URL oracle = new URL(path);
         HttpURLConnection yc = (HttpURLConnection) oracle.openConnection();
         yc.setRequestMethod(method);

        int codereponse = 0;
        String reponse = "";
        try {

            codereponse = yc.getResponseCode();

            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                reponse += inputLine;
            }
            in.close();

        } catch (IOException e) {
            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getErrorStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                reponse += inputLine;
            }
            in.close();
        }

        return new Triplet<Integer, String>(codereponse, reponse, parametres);

    }

}




