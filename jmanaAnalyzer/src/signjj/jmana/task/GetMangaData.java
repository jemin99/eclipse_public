package signjj.jmana.task;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GetMangaData {
	@SuppressWarnings("unchecked")
	public JSONArray getMangaList(String list_base_url, String page_base_url, String manga_title) {
		try {
			Document doc = Jsoup
					.connect(page_base_url+manga_title)
					.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36")
					.get();
			
			//Elements mangasEle = doc.select("div.contents > ul > li > a[href]");
			Elements mangasEle = doc.select("div.post-content-list > h2 > a[href]");
			
			JSONArray mangasArr = new JSONArray();
			
			for(int i=mangasEle.size()-1; i>=0; i--) {
				Element mangaEle = mangasEle.get(i);

				String url = mangaEle.attr("href");
				url = url.split("/book/")[1];
				//url = url.replace("/book/", "");
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
			e.printStackTrace();
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
			Document doc = Jsoup
					.connect(page_base_url+manga_url)
					.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36")
					.get();
			
			Elements pagesEle = doc.select("div.center > img[data-src]");
			
			JSONArray pagesArr = new JSONArray();
			
			for(int i=0; i<pagesEle.size(); i++) {
				Element pageEle = pagesEle.get(i);
				
				String src = pageEle.attr("data-src");
				
				JSONObject pageJSON = new JSONObject();
				pageJSON.put("title", manga_title);
				pageJSON.put("vol", manga_vol);
				pageJSON.put("page", i);
				pageJSON.put("url", src);
				
				pagesArr.add(pageJSON);
			}
			
			return pagesArr;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
