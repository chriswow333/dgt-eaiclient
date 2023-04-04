package dgt.eaiclient.decoder;

import feign.Response;
import feign.codec.ErrorDecoder;

public class DgtEaiClientErrorDecoder implements ErrorDecoder{
  
  public Exception decode(String methodKey, Response response){
    

    switch (response.status()){
      case 400:
          // return new BadRequestException();
      case 404:
          // return new NotFoundException();
      default:
          return new Exception("Generic error");
  }

    // try {

    //   System.out.println("exception: " + methodKey);
      
    //   System.out.println(response.body().asReader(StandardCharsets.UTF_8));
    

    //   Map<String, String> mdcMap = MDC.getCopyOfContextMap();
    //   for(String key: mdcMap.keySet()){
    //     mdcMap.get(key);
    //   }
      
    // }catch(IOException e) {

    //   e.printStackTrace();

    // }

    // return new Exception("error");
  }

}
