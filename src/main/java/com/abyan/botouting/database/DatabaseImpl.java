package com.abyan.botouting.database;

import com.abyan.botouting.model.Menu;
import com.abyan.botouting.model.Order;
import com.abyan.botouting.model.PJDanus;
import com.abyan.botouting.model.PJWarkop;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

public class DatabaseImpl implements Database {
    private final static String SQL_SELECT_ALL2 = "SELECT id_pemesanan, nama, pesanan, total, hutang, tanggal, jam FROM pemesanan";
    private final static String SQL_GET_BY_ORDER_ID = SQL_SELECT_ALL2 + " WHERE id_pemesanan = ?;";
    private final static String SQL_NEW_ORDER = "INSERT INTO pemesanan (nama, pesanan, total, hutang, tanggal, jam) " +
            "VALUES (?, ?, ?, ?, ?, ?)";
    private final static String SQL_DELETE_ORDER = "DELETE FROM pemesanan WHERE id_pemesanan = ?";
    private final static String SQL_LATEST_ID = "SELECT MAX(id_pemesanan) FROM pemesanan";
    private final static String SQL_TOTAL = "SELECT x.count, y.sum, z.sum FROM " +
            "(SELECT COUNT(id_pemesanan) FROM pemesanan WHERE tanggal=?) as x, " +
            "(SELECT SUM(total) FROM pemesanan WHERE tanggal=?) as y, " +
            "(SELECT SUM(hutang) FROM pemesanan WHERE tanggal=?) as z;";
    private final static String SQL_BAYAR = "UPDATE pemesanan SET hutang=hutang-? WHERE id_pemesanan=?";
    private final static String SQL_GET_HUTANG = "SELECT * FROM pemesanan WHERE hutang>0 ORDER BY id_pemesanan";
    private final static String SQL_GET_ORDER_HARIINI = "SELECT * FROM pemesanan WHERE tanggal=? ORDER BY id_pemesanan";
    private final static String SQL_UPDATE_ORDER = "UPDATE pemesanan SET pesanan=CONCAT(pesanan, ?), hutang=hutang+?, total=total+? WHERE id_pemesanan=?";

    private final static String SQL_ADD_NEW_PJ_DANUS = "UPDATE listpjdanus SET namadagang=CONCAT(namadagang, ?) WHERE id_hari=?;";
    private final static String SQL_ADD_NEW_PJ_MESEN = "UPDATE listpjdanus SET namapesen=CONCAT(namapesen, ?) WHERE id_hari=?;";
    private final static String SQL_DEL_PJ_DANUS = "UPDATE listpjdanus SET namadagang=REPLACE(namadagang, ?, '') WHERE id_hari=?";
    private final static String SQL_DEL_PJ_MESEN = "UPDATE listpjdanus SET namapesen=REPLACE(namapesen, ?, '') WHERE id_hari=?";
    private final static String SQL_GET_PJ = "SELECT * FROM listpjdanus ORDER BY id_hari";
    private final static String SQL_GET_PJ_BY_DAY = "SELECT * FROM listpjdanus WHERE id_hari=?";

    private final static String SQL_GET_LIST_PJ_WARKOP = "SELECT * FROM pjwarkop ORDER BY id_hari;";
    private final static String SQL_GET_LIST_PJ_WARKOP_BY_DAY = "SELECT * FROM pjwarkop WHERE id_hari=?";
    private final static String SQL_ADD_PJ_WARKOP_HARIAN = "UPDATE pjwarkop SET nama=CONCAT(nama, ?) WHERE id_hari=?;";
    private final static String SQL_ADD_PJ_WARKOP_HARIAN_SESI1 = "UPDATE pjwarkop SET sesi1=CONCAT(sesi1, ?) WHERE id_hari=?;";
    private final static String SQL_ADD_PJ_WARKOP_HARIAN_SESI2 = "UPDATE pjwarkop SET sesi2=CONCAT(sesi2, ?) WHERE id_hari=?;";
    private final static String SQL_ADD_PJ_WARKOP_HARIAN_SESI3 = "UPDATE pjwarkop SET sesi3=CONCAT(sesi3, ?) WHERE id_hari=?;";
    private final static String SQL_DEL_PJ_WARKOP_HARIAN = "UPDATE pjwarkop SET nama=REPLACE(nama, ?, '') WHERE id_hari=?;";
    private final static String SQL_DEL_PJ_WARKOP_HARIAN_SESI1 = "UPDATE pjwarkop SET sesi1=REPLACE(sesi1, ?, '') WHERE id_hari=?;";
    private final static String SQL_DEL_PJ_WARKOP_HARIAN_SESI2 = "UPDATE pjwarkop SET sesi2=REPLACE(sesi2, ?, '') WHERE id_hari=?;";
    private final static String SQL_DEL_PJ_WARKOP_HARIAN_SESI3 = "UPDATE pjwarkop SET sesi3=REPLACE(sesi3, ?, '') WHERE id_hari=?;";

    private final static String SQL_INSERT_MENU = "INSERT INTO menu (nama, harga, kategori) VALUES (?, ?, ?);";
    private final static String SQL_DELETE_MENU = "DELETE FROM menu WHERE nama=?";
    private final static String SQL_GET_MENU = "SELECT * FROM menu WHERE nama=? ORDER BY kategori";
    private final static String SQL_GET_LIST_MENU_MAKANAN = "SELECT * FROM menu WHERE kategori='makanan'";
    private final static String SQL_GET_LIST_MENU_MINUMAN = "SELECT * FROM menu WHERE kategori='minuman'";

//----------------------------------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------------------------

    private JdbcTemplate mJdbc;

    private final static ResultSetExtractor<Order> ORDER_SINGLE_RS_EXTRACTOR = new ResultSetExtractor<Order>() {
        @Override
        public Order extractData(ResultSet aRs)
                throws SQLException, DataAccessException {
            while (aRs.next()) {
                Order o = new Order(
                        aRs.getInt("id_pemesanan"),
                        aRs.getString("nama"),
                        aRs.getString("pesanan"),
                        aRs.getInt("total"),
                        aRs.getInt("hutang"),
                        aRs.getString("tanggal"),
                        aRs.getString("jam"));

                return o;
            }
            return null;
        }
    };

    private final static ResultSetExtractor<List<Order>> ORDER_MULTIPLE_RS_EXTRACTOR = new ResultSetExtractor<List<Order>>() {
        @Override
        public List<Order> extractData(ResultSet aRs)
                throws SQLException, DataAccessException {
            List<Order> list = new Vector<Order>();
            while (aRs.next()) {
                Order o = new Order(
                        aRs.getInt("id_pemesanan"),
                        aRs.getString("nama"),
                        aRs.getString("pesanan"),
                        aRs.getInt("total"),
                        aRs.getInt("hutang"),
                        aRs.getString("tanggal"),
                        aRs.getString("jam"));
                list.add(o);
            }
            return list;
        }
    };

    private final static ResultSetExtractor<PJDanus> PJ_EXTRACTOR = new ResultSetExtractor<PJDanus>() {
        @Override
        public PJDanus extractData(ResultSet aRs)
                throws SQLException, DataAccessException {
            while (aRs.next()) {
                PJDanus PJD = new PJDanus(
                        aRs.getString("hari"),
                        aRs.getString("namadagang"),
                        aRs.getString("namapesen"));

                return PJD;
            }
            return null;
        }
    };

    private final static ResultSetExtractor<List<PJDanus>> PJ_MULTIPLE_RS_EXTRACTOR = new ResultSetExtractor<List<PJDanus>>() {
        @Override
        public List<PJDanus> extractData(ResultSet aRs)
                throws SQLException, DataAccessException {
            List<PJDanus> list = new Vector<>();
            while (aRs.next()) {
                PJDanus PJD = new PJDanus(
                        aRs.getString("hari"),
                        aRs.getString("namadagang"),
                        aRs.getString("namapesen"));
                list.add(PJD);
            }
            return list;
        }
    };

    private final static ResultSetExtractor<List<PJWarkop>> PJ_WARKOP_MULTIPLE_RS_EXTRACTOR = new ResultSetExtractor<List<PJWarkop>>() {
        @Override
        public List<PJWarkop> extractData(ResultSet aRs)
                throws SQLException, DataAccessException {
            List<PJWarkop> list = new Vector<>();
            while (aRs.next()) {
                PJWarkop PJW = new PJWarkop(
                        aRs.getString("hari"),
                        aRs.getString("nama"),
                        aRs.getString("sesi1"),
                        aRs.getString("sesi2"),
                        aRs.getString("sesi3"));
                list.add(PJW);
            }
            return list;
        }
    };

    private final static ResultSetExtractor<PJWarkop> PJ_WARKOP_EXTRACTOR = new ResultSetExtractor<PJWarkop>() {
        @Override
        public PJWarkop extractData(ResultSet aRs)
                throws SQLException, DataAccessException {
            while (aRs.next()) {
                PJWarkop PJW = new PJWarkop(
                        aRs.getString("hari"),
                        aRs.getString("nama"),
                        aRs.getString("sesi1"),
                        aRs.getString("sesi2"),
                        aRs.getString("sesi3"));

                return PJW;
            }
            return null;
        }
    };

    private final static ResultSetExtractor<List<Menu>> MENU_MULTIPLE_RS_EXTRACTOR = new ResultSetExtractor<List<Menu>>() {
        @Override
        public List<Menu> extractData(ResultSet aRs)
                throws SQLException, DataAccessException {
            List<Menu> list = new Vector<Menu>();
            while (aRs.next()) {
                Menu menu = new Menu(
                        aRs.getString("nama"),
                        aRs.getInt("harga"),
                        aRs.getString("kategori"));
                list.add(menu);
            }
            return list;
        }
    };

    private final static ResultSetExtractor<Menu> MENU_EXTRACTOR = new ResultSetExtractor<Menu>() {
        @Override
        public Menu extractData(ResultSet aRs)
                throws SQLException, DataAccessException {
            while (aRs.next()) {
                Menu m = new Menu(
                        aRs.getString("nama"),
                        aRs.getInt("harga"),
                        aRs.getString("kategori"));
                return m;
            }
            return null;
        }
    };

    private final static ResultSetExtractor<Order> ORDER_ID_EXTRACTOR = new ResultSetExtractor<Order>() {
        @Override
        public Order extractData(ResultSet aRs)
                throws SQLException, DataAccessException {
            while (aRs.next()) {
                Order o = new Order(
                        aRs.getInt("max"), "", "", 0, 0, "", "");
                return o;
            }
            return null;
        }
    };

    public Order getLatestID() {
        return mJdbc.query(SQL_LATEST_ID, ORDER_ID_EXTRACTOR);
    }

    private final static ResultSetExtractor<Integer[]> TODAY_TOTAL_EXTRACTOR = new ResultSetExtractor<Integer[]>() {
        @Override
        public Integer[] extractData(ResultSet aRs)
                throws SQLException, DataAccessException {
            while (aRs.next()) {
                Integer[] i = new Integer[3];
                i[0] = aRs.getInt(1);
                i[1] = aRs.getInt(2);
                i[2] = aRs.getInt(3);
                return i;
            }
            return null;
        }
    };

    @Override
    public Integer[] getTotalOrder(String tanggal) {
        return mJdbc.query(SQL_TOTAL, new Object[]{tanggal, tanggal, tanggal}, TODAY_TOTAL_EXTRACTOR);
    }

    @Override
    public List<Order> getByOrderID(int id) {
        return mJdbc.query(SQL_GET_BY_ORDER_ID, new Object[]{id}, ORDER_MULTIPLE_RS_EXTRACTOR);
    }

    @Override
    public int registerOrder(String nama, String pemesanan, int total, String tanggal, String jam) {
        return mJdbc.update(SQL_NEW_ORDER, nama, pemesanan, total, total, tanggal, jam);
    }

    public List<Order> getHutang() {
        return mJdbc.query(SQL_GET_HUTANG, ORDER_MULTIPLE_RS_EXTRACTOR);
    }

    public List<Order> getOrder(String tanggal) {
        return mJdbc.query(SQL_GET_ORDER_HARIINI, new Object[]{tanggal}, ORDER_MULTIPLE_RS_EXTRACTOR);
    }

    @Override
    public List<PJDanus> getPJDanus(String hari) {
        if (hari.equalsIgnoreCase("semua"))
            return mJdbc.query(SQL_GET_PJ, PJ_MULTIPLE_RS_EXTRACTOR);
        else
            return mJdbc.query(SQL_GET_PJ_BY_DAY, new Object[]{getIdHari(hari)}, PJ_MULTIPLE_RS_EXTRACTOR);
    }

    @Override
    public List<PJWarkop> getPJWarkop(String hari) {
        if (hari.equalsIgnoreCase("semua"))
            return mJdbc.query(SQL_GET_LIST_PJ_WARKOP, PJ_WARKOP_MULTIPLE_RS_EXTRACTOR);
        else
            return mJdbc.query(SQL_GET_LIST_PJ_WARKOP_BY_DAY, new Object[]{getIdHari(hari)}, PJ_WARKOP_MULTIPLE_RS_EXTRACTOR);
    }

    @Override
    public Menu getMenuDetail(String nama) {
        return mJdbc.query(SQL_GET_MENU, new Object[]{nama}, MENU_EXTRACTOR);
    }

    @Override
    public List<Menu> getListMenuMakanan() {
        return mJdbc.query(SQL_GET_LIST_MENU_MAKANAN, new Object[]{}, MENU_MULTIPLE_RS_EXTRACTOR);
    }
    @Override
    public List<Menu> getListMenuMinuman() {
        return mJdbc.query(SQL_GET_LIST_MENU_MINUMAN, new Object[]{}, MENU_MULTIPLE_RS_EXTRACTOR);
    }

    public int bayarOrder(int orderId, int jumlah) {
        return mJdbc.update(SQL_BAYAR, jumlah, orderId);
    }

    public int updateOrder(int id, String order, int harga) {
        return mJdbc.update(SQL_UPDATE_ORDER, order, harga, harga, id);
    }

    @Override
    public int updatePJWarkop(String hari, String nama, int sesi) {
        switch (sesi) {
            case 0:
                return mJdbc.update(SQL_ADD_PJ_WARKOP_HARIAN, " " + nama, getIdHari(hari));
            case 1:
                return mJdbc.update(SQL_ADD_PJ_WARKOP_HARIAN_SESI1, " " + nama, getIdHari(hari));
            case 2:
                return mJdbc.update(SQL_ADD_PJ_WARKOP_HARIAN_SESI2, " " + nama, getIdHari(hari));
            case 3:
                return mJdbc.update(SQL_ADD_PJ_WARKOP_HARIAN_SESI3, " " + nama, getIdHari(hari));
        }
        return 0;
    }

    @Override
    public int deletePJWarkop(String hari, String nama, int sesi) {
        switch (sesi) {
            case 0:
                return mJdbc.update(SQL_DEL_PJ_WARKOP_HARIAN, " " + nama, getIdHari(hari));

            case 1:
                return mJdbc.update(SQL_DEL_PJ_WARKOP_HARIAN_SESI1, " " + nama, getIdHari(hari));

            case 2:
                return mJdbc.update(SQL_DEL_PJ_WARKOP_HARIAN_SESI2, " " + nama, getIdHari(hari));

            case 3:
                return mJdbc.update(SQL_DEL_PJ_WARKOP_HARIAN_SESI3, " " + nama, getIdHari(hari));
        }
        return 0;
    }

    @Override
    public int updatePJ(String hari, String nama, String mode) {
        if (mode.equalsIgnoreCase("danus")) {
            System.out.println("Masuk ke danus");
            return mJdbc.update(SQL_ADD_NEW_PJ_DANUS, " " + nama, getIdHari(hari));
        } else if (mode.equalsIgnoreCase("mesen")) {
            System.out.println("Masuk ke mesen");
            return mJdbc.update(SQL_ADD_NEW_PJ_MESEN, " " + nama, getIdHari(hari));
        }
        System.out.println("Ga masuk dua duanya");
        return 0;
    }

    @Override
    public int deletePJ(String hari, String nama, String mode) {
        if (mode.equalsIgnoreCase("danus")) {
            System.out.println("Masuk ke danus");
            return mJdbc.update(SQL_DEL_PJ_DANUS, " " + nama, getIdHari(hari));
        } else if (mode.equalsIgnoreCase("mesen")) {
            System.out.println("Masuk ke mesen");
            return mJdbc.update(SQL_DEL_PJ_MESEN, " " + nama, getIdHari(hari));
        }
        System.out.println("Ga masuk dua duanya");
        return 0;
    }

    @Override
    public int updateMenu(String nama, int harga, String kategori) {
        return mJdbc.update(SQL_INSERT_MENU, nama, harga, kategori);
    }

    @Override
    public int deleteMenu(String nama) {
        return mJdbc.update(SQL_DELETE_MENU, nama);
    }

    public int deleteOrder(int id) {
        return mJdbc.update(SQL_DELETE_ORDER, id);
    }

    public DatabaseImpl(DataSource aDataSource) {
        mJdbc = new JdbcTemplate(aDataSource);
    }

    public int getIdHari(String hari) {
        int h;
        switch (hari.toLowerCase()) {
            case "senin":
                h = 1;
                break;
            case "selasa":
                h = 2;
                break;
            case "rabu":
                h = 3;
                break;
            case "kamis":
                h = 4;
                break;
            case "jumat":
                h = 5;
                break;
            case "sabtu":
                h = 6;
                break;
            case "minggu":
                h = 7;
                break;
            default:
                h = 0;
                break;
        }
        return h;
    }
}