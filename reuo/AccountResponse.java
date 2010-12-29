package reuo;

	/**
 * Describes the 
 * @author Kristopher Ives
 */
public enum AccountResponse{
	INVALID					(0x00),
	IN_USE					(0x01),
	BLOCK					(0x02),
	INVALID_CREDENTIALS		(0x03),
	COMMUNICATION_ERROR		(0x04),
	CONCURRENCY_LIMIT		(0x05),
	TIMEOUT					(0x06),
	AUTHENTICATION_FAILURE	(0x07),
	/* Extended LoginResponses */
	LOGGED_IN					(-1);
	
	final int id;
	
	AccountResponse(int id){
		this.id = id;
	}
	
	public int getIdentifier(){
		return(id);
	}
	
	public static AccountResponse fromIdentifier(int id){
		AccountResponse[] responses = AccountResponse.values();
		
		if(id < 0 || id >= responses.length){
			return(null);
		}
		
		return(responses[id]);			
	}
}