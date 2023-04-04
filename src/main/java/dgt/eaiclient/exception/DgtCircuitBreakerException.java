package dgt.eaiclient.exception;

public class DgtCircuitBreakerException extends RuntimeException{
  
  public DgtCircuitBreakerException(String message){
    super(message);
  }
}
