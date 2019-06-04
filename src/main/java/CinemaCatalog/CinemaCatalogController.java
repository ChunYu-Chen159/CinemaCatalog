package CinemaCatalog;

import org.springframework.web.bind.annotation.RestController;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;




@Api(value = "CinemaCatalogController", tags = "與電影相關的所有一切都在這裡")
@RestController
public class CinemaCatalogController {
	
	@Autowired
	FeignInterface feignInterface;
	
	@ApiOperation(value = "測試此伺服器是否成功連線", notes = "成功連線就回傳success")
	@CrossOrigin(origins = "*")
	@RequestMapping(value="/", method = RequestMethod.GET)
    public String index() 
    {
		return "success";
    }
	
	@ApiOperation(value = "拿到所有的電影資料", notes = "回傳資料")
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/getAllMovies", method = RequestMethod.GET)
    public String getAllMovies()
    {
    	return CinemaCatalog.getAllMovies();
    }
	
	@ApiOperation(value = "利用ID找到某個電影", notes = "回傳某電影資料")
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/getMovieByID", method = RequestMethod.GET)
    public String getMovieByID(@ApiParam(required = true, name = "userID", value = "使用者編號") @RequestParam("userID") String userID)
    {
String result = "";
		
		
		try {
			
			result = "[";
			
			/*
			URL url = new URL("http://140.121.196.23:4139/ordering/getMovieFromOrderList?userID=1");
			org.jsoup.nodes.Document xmlDoc =  Jsoup.parse(url, 3000);
			String jaStr = xmlDoc.select("body").get(0).text();
			*/
			
			System.out.println("-----------------------------------------------------------------------");
			System.out.println(feignInterface.getMovieByID(userID));
			System.out.println("-----------------------------------------------------------------------");
			
			String jaStr = feignInterface.getMovieByID(userID);
			

			
			JSONArray jsonArray = JSONArray.fromObject(jaStr);
			
			
			System.out.println("MongoDBConnect to database begin");
			
            MongoClient mongoClient = MongoClients.create("mongodb://cinema:cinema@140.121.196.23:4118");
            
            MongoDatabase mongoDatabase = mongoClient.getDatabase("Movies");
            System.out.println("MongoDBConnect to database successfully");
            
            MongoCollection<Document> collection = mongoDatabase.getCollection("Movie");
            
            
            for(int i = 0; i < jsonArray.size(); i++) {
            	JSONObject jsonObject = jsonArray.getJSONObject(i);
            	
            	BasicDBObject whereQuery = new BasicDBObject();
            	
            	whereQuery.put("_id", new ObjectId(jsonObject.getString("ObjectID")));
            	
                FindIterable<Document> fi = collection.find(whereQuery);
                MongoCursor<Document> cursor = fi.iterator();
                while(cursor.hasNext()) 
                {
                	result += cursor.next().toJson();
                }
            	
                if(i < jsonArray.size() - 1)
                	result += ",";
            }
			
			
			result += "]";
			
			return result;
			
			
		} catch (Exception e) {
			e.printStackTrace();
			return "{}";
		}
    }
	
	@ApiOperation(value = "拿到所有通知", notes = "回傳通知資料")
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/getNotification", method = RequestMethod.GET)
    public String getNotification(@ApiParam(required = true, name = "userID", value = "使用者編號") @RequestParam("userID") String userID)
    {
    	return CinemaCatalog.getNotification(userID);
    }
	
	@ApiOperation(value = "購買電影", notes = "購買成功就回傳成功")
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/orderingMovie", method = RequestMethod.GET)
    public String orderingMovie(@ApiParam(required = true, name = "ID", value = "電影編號")@RequestParam("moviesID") String moviesID)
    {
    	return CinemaCatalog.orderingMovie(moviesID);
    }
	
}



