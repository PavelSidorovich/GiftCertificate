package com.epam.esm.gcs.manager;

import java.sql.SQLException;

public interface TestDatabaseManager {

    void cleanAndPopulateTables() throws SQLException;

}