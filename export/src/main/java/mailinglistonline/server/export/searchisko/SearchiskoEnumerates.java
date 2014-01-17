package mailinglistonline.server.export.searchisko;

public class SearchiskoEnumerates {

	public enum SortBy {
		NEW ("new"),
		OLD ("old") ;
		
		private String text;

		SortBy(String text)
	    {
	        this.text=text;
	    }
	}
	
	public enum Facet {
		
	}
}
