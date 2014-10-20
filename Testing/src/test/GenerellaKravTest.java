package test;

import java.sql.SQLException;

import org.junit.*;

public class GenerellaKravTest extends PussTest {

	/**
	 * Alla typer av inloggade användare har tillgång till menyn på samtliga
	 * sidor som visas av systemet [SRS krav 6.1.1]
	 * 
	 * @throws SQLException
	 */
	@Test
	public void FT1_1_1() throws SQLException {
		addGroup("menygrupp");
		addUser("victor", "victor", 0);
		addUser("mrsmith", "mrsmith", 0);
		addUserToGroup("victor", "menygrupp", "Project Leader");
		addUserToGroup("mrsmith", "menygrupp", "t1");
		deleteUser("mrsmith");
		deleteUser("victor");
		deleteGroup("menygrupp");
	}

	@Ignore
	public void FT1_1_2() {

	}

	@Test
	public void FT1_1_3() {

	}
}
