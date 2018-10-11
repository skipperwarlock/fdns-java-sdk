import java.io.InputStream;
import java.nio.charset.Charset;

import org.junit.Test;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import gov.cdc.helper.CDAHelper;
import gov.cdc.helper.HL7Helper;
import gov.cdc.helper.IndexingHelper;
import gov.cdc.helper.ObjectHelper;
import gov.cdc.helper.StorageHelper;

public class SimpleTest {

	@Test
	public void testHL7() throws Exception {
		HL7Helper helper = HL7Helper.getInstance();

		InputStream is = SimpleTest.class.getResourceAsStream("hl7/msg.txt");
		String message = IOUtils.toString(is, Charset.defaultCharset());

		System.out.println(helper.parse(message, "hl7"));
		System.out.println(helper.parseToXML(message));
		System.out.println(helper.getCaseId(message, "hl7"));
		System.out.println(helper.getMessageHash(message));
		System.out.println(helper.validateWithRules(message, "test", true, true));
	}

	@Test
	public void testCDA() throws Exception {
		CDAHelper helper = CDAHelper.getInstance();

		InputStream is = SimpleTest.class.getResourceAsStream("cda/msg.txt");
		String message = IOUtils.toString(is, Charset.defaultCharset());

		System.out.println(helper.parse(message));
	}

	@Test
	public void testObject() throws Exception {
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
		System.out.println(helper.search("", db, collection));
		System.out.println(helper.deleteObject(objectId, db, collection));
		System.out.println(helper.deleteCollection(db, collection));
	}

	@Test
	public void testStorage() throws Exception {
		StorageHelper helper = StorageHelper.getInstance();
		String drawerName = "kldrawer";
		String fileName = "hello.txt";

		System.out.println(helper.createDrawer(drawerName));
		System.out.println(helper.getDrawer(drawerName));
		System.out.println(helper.getDrawers());
		JSONObject node = helper.createNode(drawerName, fileName, "Hello World!");
		System.out.println(node);
		String objectId = node.getString("id");
		System.out.println(helper.getNode(drawerName, objectId));
		System.out.println(helper.listNodes(drawerName, null));
		System.out.println(IOUtils.toString(helper.dowloadNode(drawerName, objectId), Charset.defaultCharset().name()));
		System.out.println(helper.updateNode(drawerName, objectId, "Hello World 2 !"));
		System.out.println(IOUtils.toString(helper.dowloadNode(drawerName, objectId), Charset.defaultCharset().name()));
		System.out.println(helper.deleteNode(drawerName, objectId));
		System.out.println(helper.deleteDrawer(drawerName));
	}

	@Test
	public void testIndexing() throws Exception {
		InputStream is = SimpleTest.class.getResourceAsStream("indexing/config.json");
		JSONObject config = new JSONObject(IOUtils.toString(is, Charset.defaultCharset()));
		String configName = "test";

		IndexingHelper helper = IndexingHelper.getInstance();
		
		JSONObject json = new JSONObject();
		json.put("hello", "world");
		String collection = "simple";
		String db = "test";

		System.out.println(helper.createOrUpdateConfig(configName, config));
		System.out.println(helper.createIndex(configName));
		
		JSONObject createdObj = ObjectHelper.getInstance().createObject(json, db, collection);
		System.out.println(createdObj);
		String objectId = createdObj.getJSONObject("_id").getString("$oid");
		
		System.out.println(helper.indexObject(configName, objectId));
		System.out.println(helper.getIndex(configName, objectId));
		System.out.println(helper.search(configName, "", false, 0, 10));
		
		System.out.println(helper.deleteIndex(configName));
	}
}
