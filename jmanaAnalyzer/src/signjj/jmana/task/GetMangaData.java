package signjj.jmana.task;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GetMangaData {
	@SuppressWarnings("unchecked")
	public JSONArray getMangaList(String list_base_url, String manga_title) {
		try {
			Document doc = Jsoup.connect(list_base_url+manga_title).get();
			
			Elements mangasEle = doc.select("div.contents > ul > li > a[href]");
			
			JSONArray mangasArr = new JSONArray();
			
			for(int i=mangasEle.size()-1; i>=0; i--) {
				Element mangaEle = mangasEle.get(i);

				String url = mangaEle.attr("href");
				url = url.replace("/book/", "");
				String vol = mangaEle.html();
				
				boolean isContains = contains(mangasArr, "vol", vol);
				
				if(!isContains) {
					JSONObject mangaJSON = new JSONObject();
					mangaJSON.put("title", manga_title);
					mangaJSON.put("vol", vol);
					mangaJSON.put("url", url);
					
					mangasArr.add(mangaJSON);
				}
			}
			
			return mangasArr;
		} catch(Exception e) {
			return null;
		}
	}
	
	private boolean contains(JSONArray arr, String key, String value) {
		boolean result = false;
		
		for(int i=0; i<arr.size(); i++) {
			JSONObject obj = (JSONObject) arr.get(i);
			String origin_value = (String) obj.get(key);
			if(origin_value.equals(value)) {
				result = true;
				break;
			}
		}
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray getMangaPage(String manga_title, String manga_vol, String page_base_url, String manga_url) {
		try {
			Document doc = Jsoup.connect(page_base_url+manga_url).get();
			
			Elements pagesEle = doc.select("div.center > img[src]");
			
			JSONArray pagesArr = new JSONArray();
			
			for(int i=0; i<pagesEle.size(); i++) {
				Element pageEle = pagesEle.get(i);
				
				String src = pageEle.attr("src");
				
				JSONObject pageJSON = new JSONObject();
				pageJSON.put("title", manga_title);
				pageJSON.put("vol", manga_vol);
				pageJSON.put("page", i);
				pageJSON.put("url", src);
				
				pagesArr.add(pageJSON);
			}
			
			return pagesArr;
		} catch(Exception e) {
			return null;
		}
	}
}
