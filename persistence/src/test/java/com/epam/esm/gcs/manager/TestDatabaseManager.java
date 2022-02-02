package com.epam.esm.gcs.manager;

import java.sql.SQLException;

public interface TestDatabaseManager {

    void dropCreateAndPopulateTables() throws SQLException;

}
