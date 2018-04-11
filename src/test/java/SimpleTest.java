import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import gov.cdc.helper.CDAHelper;
import gov.cdc.helper.HL7Helper;
import gov.cdc.helper.IndexingHelper;
import gov.cdc.helper.ObjectHelper;
import gov.cdc.helper.StorageHelper;

public class SimpleTest {

	public static void main(String[] args) {
		try {
			// testHL7();
			// testCDA();
			// testObject();
			// testStorage();
			testIndexing();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void testHL7() throws Exception {
		HL7Helper helper = HL7Helper.getInstance();

		InputStream is = SimpleTest.class.getResourceAsStream("hl7/msg.txt");
		String message = IOUtils.toString(is, Charset.defaultCharset());

		System.out.println(helper.parse(message, "phinms"));
		System.out.println(helper.parseToXML(message));
		System.out.println(helper.getCaseId(message, "phinms"));
		System.out.println(helper.getMessageHash(message));
		System.out.println(helper.validateWithRules(message, "test", true, true));
		System.out.println(helper.validateWithIG(message, "test", "ORU_R01_Profile.xml", "ValueSets.xml", "CContext.xml"));
	}

	private static void testCDA() throws Exception {
		CDAHelper helper = CDAHelper.getInstance();

		InputStream is = SimpleTest.class.getResourceAsStream("cda/msg.txt");
		String message = IOUtils.toString(is, Charset.defaultCharset());

		System.out.println(helper.parse(message));
	}

	private static void testObject() throws Exception {
		ObjectHelper helper = ObjectHelper.getInstance();

		JSONObject json = new JSONObject();
		json.put("hello", "world");
		JSONObject updateCommand = new JSONObject("{'hello':'world2'}");
		String collection = "kltest";
		String db = "kltest";

		JSONObject createdObj = helper.createObject(json, db, collection);
		System.out.println(createdObj);
		String objectId = createdObj.getJSONObject("_id").getString("$oid");
		System.out.println(helper.createObject(json, "customid", db, collection));
		System.out.println(helper.updateObject(objectId, updateCommand, db, collection));
		System.out.println(helper.getObject(objectId, db, collection));
		System.out.println(helper.countObjects(new JSONObject(), db, collection));
		System.out.println(helper.aggregate(new JSONArray("[ { $group: { _id: '$hello', hello: { $sum: 1 } } }, { $sort: { count: -1  } } ]"), db, collection));
		System.out.println(helper.distinct(new JSONObject(), "hello", db, collection));
		System.out.println(helper.find(new JSONObject(), db, collection));
		System.out.println(helper.deleteObject(objectId, db, collection));
		System.out.println(helper.deleteCollection(db, collection));
	}

	private static void testStorage() throws Exception {
		StorageHelper helper = StorageHelper.getInstance();
		String drawerName = "kldrawer";
		String fileName = "hello.txt";

		System.out.println(helper.createDrawer(drawerName));
		System.out.println(helper.getDrawer(drawerName));
		System.out.println(helper.getDrawers());
		JSONObject node = helper.createNode(drawerName, fileName, "Hello World!");
		String objectId = node.getString("id");
		System.out.println(node);
		System.out.println(helper.getNode(drawerName, objectId));
		System.out.println(helper.listNodes(drawerName, null));
		System.out.println(IOUtils.toString(helper.dowloadNode(drawerName, objectId), Charset.defaultCharset().name()));
		System.out.println(helper.updateNode(drawerName, objectId, "Hello World 2 !"));
		System.out.println(IOUtils.toString(helper.dowloadNode(drawerName, objectId), Charset.defaultCharset().name()));
		System.out.println(helper.deleteNode(drawerName, objectId));
		System.out.println(helper.deleteDrawer(drawerName));
	}

	private static void testIndexing() throws Exception {
		IndexingHelper helper = IndexingHelper.getInstance();
		String type = "ngmvps.message";
		
		JSONObject json = new JSONObject();
		json.put("hello", "world");
		String collection = "messages";
		String db = "ngmvps";

		System.out.println(helper.createIndex(type));
		
		JSONObject createdObj = ObjectHelper.getInstance().createObject(json, db, collection);
		System.out.println(createdObj);
		String objectId = createdObj.getJSONObject("_id").getString("$oid");
		
		System.out.println(helper.indexObject(type, objectId));
		System.out.println(helper.getIndex(type, objectId));
		System.out.println(helper.search(type, "", false, 0, 10));
		
		System.out.println(helper.deleteIndex(type));
	}
}
