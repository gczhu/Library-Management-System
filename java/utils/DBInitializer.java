package utils;

public interface DBInitializer {
    String sqlDropUsers();
    String sqlDropBook();
    String sqlDropCard();
    String sqlDropBorrow();
    String sqlCreateUsers();
    String sqlCreateBook();
    String sqlCreateCard();
    String sqlCreateBorrow();

}
