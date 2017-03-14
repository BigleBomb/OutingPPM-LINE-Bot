package com.abyan.botouting.database;

import com.abyan.botouting.model.Menu;
import com.abyan.botouting.model.Order;
import com.abyan.botouting.model.PJDanus;
import com.abyan.botouting.model.PJWarkop;
import com.sun.org.apache.xpath.internal.operations.Or;

import java.util.List;

public interface Database
{
    public List<Order> getByOrderID(int id);
    public Order getLatestID();
    public Integer[] getTotalOrder(String tanggal);
    public List<Order> getHutang();
    public List<Order> getOrder(String tanggal);
    public List<PJDanus> getPJDanus(String hari);
    public Menu getMenuDetail(String nama);
    public List<Menu> getListMenuMakanan();
    public List<Menu> getListMenuMinuman();
    public List<PJWarkop> getPJWarkop(String hari);
    public int registerOrder(String nama, String pemesanan, int total, String tanggal, String jam);
    public int deleteOrder(int i);
    public int bayarOrder(int orderId, int jumlah);
    public int updateOrder(int id, String order, int jumlah);
    public int updatePJWarkop(String hari, String nama, int sesi);
    public int deletePJWarkop(String hari, String nama, int sesi);
    public int updatePJ(String hari, String nama, String mode);
    public int deletePJ(String hari, String nama, String mode);
    public int updateMenu(String nama, int harga, String kategori);
    public int deleteMenu(String nama);
}