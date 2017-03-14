package com.abyan.botouting.controller;

import com.abyan.botouting.database.Database;
import com.abyan.botouting.model.*;
import com.google.gson.Gson;
import com.linecorp.bot.client.LineMessagingServiceBuilder;
import com.linecorp.bot.client.LineSignatureValidator;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.template.ButtonsTemplate;
import com.linecorp.bot.model.profile.UserProfileResponse;
import com.linecorp.bot.model.response.BotApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import retrofit2.Response;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(value = "/linebot")
public class LineBotController {
    //inisialisasi channel secret
    @Autowired
    @Qualifier("com.linecorp.channel_secret")
    String lChannelSecret;

    //inisialisasi channel access token
    @Autowired
    @Qualifier("com.linecorp.channel_access_token")
    String lChannelAccessToken;

    @Autowired
    Database mDatabase;

    private String displayName;
    private Payload payload;

    private Chat chat = new Chat();

    private String[] cmdlist = {
            "addorder",
            "orderid",
            "deleteorder",
            "orderhariini",
            "ordertanggal",
            "bayar",
            "hutang",
            "totalorder",
            "tambahpesanan",
            "listmenu",
            "addmenu",
            "deletemenu"
    };

    private String[] cmdlist2 = {
            "pjdanus",
            "addpjdanus",
            "deletepjdanus",
            "pjwarkop",
            "addpjwarkop",
            "deletepjwarkop"
    };

    private String[] day = {
            "Senin",
            "Selasa",
            "Rabu",
            "Kamis",
            "Jumat",
            "Sabtu",
            "Minggu"
    };

    private int CmdProc = 0;
    long beginTime = System.currentTimeMillis();
    int delay = 3;
    long antiSpam = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(delay);

    @RequestMapping(value = "/callback", method = RequestMethod.POST)
    public ResponseEntity<String> callback(
            @RequestHeader("X-Line-Signature") String aXLineSignature,
            @RequestBody String aPayload) {
        // compose body
        final String text = String.format("The Signature is: %s",
                (aXLineSignature != null && aXLineSignature.length() > 0) ? aXLineSignature : "N/A");

        System.out.println(text);

        final boolean valid = new LineSignatureValidator(lChannelSecret.getBytes()).validateSignature(aPayload.getBytes(), aXLineSignature);

        System.out.println("The signature is: " + (valid ? "valid" : "tidak valid"));

        //Get events from source
        if (aPayload != null && aPayload.length() > 0) {
            System.out.println("Payload: " + aPayload);
        }

        Gson gson = new Gson();
        payload = gson.fromJson(aPayload, Payload.class);

        //Variable initialization
        String msgText = " ";
        String idTarget = " ";
        String eventType = payload.events[0].type;

        //Get event's type
        if (eventType.equals("join")) {
            if (payload.events[0].source.type.equals("group")) {
                replyToUser(payload.events[0].replyToken, "Hai grup, untuk saat ini bot hanya support pesanan warkop");
            }
            if (payload.events[0].source.type.equals("room")) {
                replyToUser(payload.events[0].replyToken, "Hai room, untuk saat ini bot hanya support pesanan warkop");
            }
        } else if (eventType.equals("follow")) {
            greetingMessage();
        } else if (eventType.equals("message")) {    //event type is message
            if (payload.events[0].source.type.equals("group")) {
                idTarget = payload.events[0].source.groupId;
            } else if (payload.events[0].source.type.equals("room")) {
                idTarget = payload.events[0].source.roomId;
            } else if (payload.events[0].source.type.equals("user")) {
                idTarget = payload.events[0].source.userId;
                msgText = payload.events[0].message.text;

                msgText = msgText.toLowerCase();

                if (System.currentTimeMillis() >= antiSpam) {
                    if (msgText.contains("status")) {
                        long currTime = System.currentTimeMillis() - beginTime;
                        String Msg = "";
                        Msg += "Cmd processed: " + CmdProc + "\n";
                        if (!(TimeUnit.MILLISECONDS.toHours(currTime) >= 1)) {
                            Msg += "Waktu berjalan: " + String.format("%d menit %d detik",
                                    TimeUnit.MILLISECONDS.toMinutes(currTime),
                                    TimeUnit.MILLISECONDS.toSeconds(currTime) -
                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currTime))
                            );
                        } else {
                            Msg += "Waktu berjalan: " + String.format("%d jam %d menit %d detik",
                                    TimeUnit.MILLISECONDS.toHours(currTime),
                                    TimeUnit.MILLISECONDS.toMinutes(currTime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(currTime)),
                                    TimeUnit.MILLISECONDS.toSeconds(currTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currTime))
                            );
                        }
                        pushMessage(payload.events[0].source.userId, Msg);
                        //updateMenu();
                    } else if (msgText.contains("set")) {
                        String words[] = msgText.split("\\s");
                        for (String word : words) System.out.println(word);
                        if (words.length > 2) {
                            if (words[1].contains("cmdproc")) {
                                if (words.length == 3) {
                                    CmdProc = Integer.valueOf(words[2]);
                                    pushMessage(payload.events[0].source.userId, "Cmd Proc set to " + CmdProc);
                                } else {
                                    pushMessage(payload.events[0].source.userId, "Not enough params1");
                                }
                            }
                        } else {
                            pushMessage(payload.events[0].source.userId, "Not enough params2");
                        }
                    }
                    else if (msgText.contains("spoof")){
                        if (payload.events[0].source.userId.equals("U7a3f1c3b1a71e16d4cbe3f0975e95165")) {
                            String words[] = msgText.split("\\s");
                            if(words.length > 1){
                                String Msg ="";
                                for(int i=1; i<words.length; i++)
                                     Msg += words[i] + " ";
                                pushMessage("C57acefe33fe937bbc266f67e1f49452a", Msg);
                            }
                        }
                    }
                    else if (isCmdText(cmdlist, msgText)) {
                        if (payload.events[0].source.userId.equals("U7a3f1c3b1a71e16d4cbe3f0975e95165")) {
                            processCmd(payload.events[0].replyToken, idTarget, msgText);
                            CmdProc++;
                            System.out.println("Command processed: " + CmdProc);
                        } else {
                            pushMessage(payload.events[0].source.userId, "Hanya Abyan yang bisa mengakses perintah tersebut.");
                        }
                    }
                    antiSpam = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(delay);
                } else {
                    System.out.println("Anti-Spam enabled remaining time until next message: " + TimeUnit.MILLISECONDS.toSeconds((antiSpam - System.currentTimeMillis())));
                }
            }
            //Parsing message from user
            if (!payload.events[0].message.type.equals("text")) {
                greetingMessage();
            } else {

                msgText = payload.events[0].message.text;
                msgText = msgText.toLowerCase();
                //getUserProfile(payload.events[0].source.userId);


                if (System.currentTimeMillis() >= antiSpam) {
                    if (!msgText.contains("bot leave")) {
                        if (isCmdText(cmdlist, msgText)) {
                            if (payload.events[0].source.groupId.equals("C7282a723095f3abf18d36f1115164512") || payload.events[0].source.groupId.equals("C3428afb4bbd4f49e0b2cc1ed1df764f1")) {
                                processCmd(payload.events[0].replyToken, idTarget, msgText);
                                CmdProc++;
                                System.out.println("Command processed: " + CmdProc);
                            } else
                                pushMessage(payload.events[0].source.groupId, "Perintah ini tidak bisa digunakan di grup ini.");
                        } else if (isCmdText(cmdlist2, msgText)) {
                            if (payload.events[0].source.groupId.equals("C57acefe33fe937bbc266f67e1f49452a") || payload.events[0].source.groupId.equals("C3428afb4bbd4f49e0b2cc1ed1df764f1") || payload.events[0].source.groupId.equals("C7282a723095f3abf18d36f1115164512")) {
                                processCmd(payload.events[0].replyToken, idTarget, msgText);
                                CmdProc++;
                                System.out.println("Command processed: " + CmdProc);
                            } else
                                pushMessage(payload.events[0].source.groupId, "Perintah ini tidak bisa digunakan di grup ini.");
                        } else if (msgText.contains("bot") || msgText.contains("capek?") || msgText.contains("lelah?")) {
                            processChat(payload.events[0].replyToken, idTarget, msgText);
                        }
                    } else {
                        if (payload.events[0].source.type.equals("group")) {
                            pushMessage(payload.events[0].source.groupId, "Dadah");
                            leaveGR(payload.events[0].source.groupId, "group");
                        } else if (payload.events[0].source.type.equals("room")) {
                            pushMessage(payload.events[0].source.groupId, "Dadah");
                            leaveGR(payload.events[0].source.roomId, "room");
                        }
                    }
                    antiSpam = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(delay);
                } else {
                    System.out.println("Anti-Spam enabled remaining time until next message: " + TimeUnit.MILLISECONDS.toSeconds(antiSpam - System.currentTimeMillis()));
                }
            }
        }

        return new ResponseEntity<String>(HttpStatus.OK);
    }

    //method untuk mengirim pesan saat ada user menambahkan bot sebagai teman
    private void greetingMessage() {
        getUserProfile(payload.events[0].source.userId);
        String greetingMsg =
                "Hi " + displayName + "! Kamu telah menambahkan akun bot Outing sebagai teman.";
//        String action = "Lihat pesanan warkop";
//        String title = "Welcome";
//        buttonTemplate(greetingMsg, action, action, title, null);
    }

    //method untuk membuat button template
    private void buttonTemplate(String message, String label, String action, String title, String ImageURL) {
        ButtonsTemplate buttonsTemplate = new ButtonsTemplate(ImageURL, null, message,
                Collections.singletonList(new MessageAction(label, action)));
        TemplateMessage templateMessage = new TemplateMessage(title, buttonsTemplate);
        PushMessage pushMessage = new PushMessage(payload.events[0].source.userId, templateMessage);
        try {
            Response<BotApiResponse> response = LineMessagingServiceBuilder
                    .create(lChannelAccessToken)
                    .build()
                    .pushMessage(pushMessage)
                    .execute();
            System.out.println(response.code() + " " + response.message());
        } catch (IOException e) {
            System.out.println("Exception is raised ");
            e.printStackTrace();
        }
    }

    //Method untuk reply message
    private void replyToUser(String rToken, String messageToUser) {
        TextMessage textMessage = new TextMessage(messageToUser);
        ReplyMessage replyMessage = new ReplyMessage(rToken, textMessage);
        try {
            Response<BotApiResponse> response = LineMessagingServiceBuilder
                    .create(lChannelAccessToken)
                    .build()
                    .replyMessage(replyMessage)
                    .execute();
            System.out.println("Reply Message: " + response.code() + " " + response.message());
        } catch (IOException e) {
            System.out.println("Exception is raised ");
            e.printStackTrace();
        }
    }

    //method untuk mendapatkan profile user (user id, display name, image, status)
    private void getUserProfile(String userId) {
        Response<UserProfileResponse> response =
                null;
        try {
            response = LineMessagingServiceBuilder
                    .create(lChannelAccessToken)
                    .build()
                    .getProfile(userId)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (response.isSuccessful()) {
            UserProfileResponse profile = response.body();
            System.out.println(profile.getDisplayName());
            System.out.println(profile.getPictureUrl());
            System.out.println(profile.getStatusMessage());
            displayName = profile.getDisplayName();
        } else {
            System.out.println(response.code() + " " + response.message());
        }
    }

    //Method untuk push message
    private void pushMessage(String sourceId, String txt) {
        TextMessage textMessage = new TextMessage(txt);
        PushMessage pushMessage = new PushMessage(sourceId, textMessage);
        try {
            Response<BotApiResponse> response = LineMessagingServiceBuilder
                    .create(lChannelAccessToken)
                    .build()
                    .pushMessage(pushMessage)
                    .execute();
            System.out.println(response.code() + " " + response.message());
        } catch (IOException e) {
            System.out.println("Exception is raised ");
            e.printStackTrace();
        }
    }

    //Method for leave group or room
    private void leaveGR(String id, String type) {
        try {
            if (type.equals("group")) {
                Response<BotApiResponse> response = LineMessagingServiceBuilder
                        .create(lChannelAccessToken)
                        .build()
                        .leaveGroup(id)
                        .execute();
                System.out.println(response.code() + " " + response.message());
            } else if (type.equals("room")) {
                Response<BotApiResponse> response = LineMessagingServiceBuilder
                        .create(lChannelAccessToken)
                        .build()
                        .leaveRoom(id)
                        .execute();
                System.out.println(response.code() + " " + response.message());
            }
        } catch (IOException e) {
            System.out.println("Exception is raised ");
            e.printStackTrace();
        }
    }

    private void processChat(String aReplyToken, String aUserId, String aText) {
        System.out.println("message text: " + aText + " from: " + aUserId);
        String[] words = aText.substring(aText.indexOf("@") + 1).split("\\s");
        String intent = words[0];
        System.out.println("intent: " + intent);
        if (intent.equalsIgnoreCase("capek?") || intent.equalsIgnoreCase("lelah?")) {
            for (int i = 0; i < chat.getLevel().length; i++) {
                if (CmdProc >= Integer.valueOf(chat.getLevel()[i][0])) {
                    pushMessage(aUserId, getRandomJawaban(i, chat.getLevel()));
                    break;
                }
            }
        } else if (intent.equalsIgnoreCase("bot")) {
            for (int i = 0; i < chat.getJawab().length; i++) {
                if (CmdProc >= Integer.valueOf(chat.getJawab()[i][0])) {
                    pushMessage(aUserId, getRandomJawaban(i, chat.getJawab()));
                    break;
                }
            }
        }
    }

    private void processCmd(String aReplyToken, String aUserId, String aText) {
        System.out.println("message text: " + aText + " from: " + aUserId);
        String[] words = aText.split("\\s");
        String intent = words[0];
        System.out.println("intent: " + intent);
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY");
        DateFormat dF = new SimpleDateFormat("HH:mm:ss");
        Date d = new Date();
        dF.setTimeZone(TimeZone.getTimeZone("GMT+7"));
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+7"));
        String tanggal = dateFormat.format(d);
        String jam = dF.format(d);

        if (intent.equalsIgnoreCase("addorder")) {
            if (words.length >= 3) {
                int total = 0;
                String fullOrder = "";
                String nama = words[1];
                nama = firstLetterUpperCase(nama);
                for (int i = 2; i < words.length; i++) {
                    if (isOnTheMenu(words[i]) != null) {
                        Menu m = isOnTheMenu(words[i]);
                        fullOrder += m.getNama() + " ";
                        total += m.getHarga();
                    } else {
                        pushMessage(aUserId, words[i] + " tidak ada di menu dan tidak ditambahkan ke pesanan");
                    }
                }
                if (!fullOrder.equals("")) {
                    Boolean test = regOrder(nama, fullOrder, total, tanggal, jam);
                    int id = getLatestID();
                    String message = nama + " memesan " + fullOrder + "dengan total:" +
                            "\nRp." + total + "" +
                            "\nID Pemesanan: " + id;
                    pushMessage(aUserId, message);
                    if (test) {
                        pushMessage(aUserId, "Pesanan berhasil diinput");
                    } else {
                        pushMessage(aUserId, "Pesanan gagal diinput");
                    }
                } else {
                    pushMessage(aUserId, "Format pesanan salah. Format: addorder <atasnama> <pesanan>");
                }
            } else {
                pushMessage(aUserId, "Format pesanan salah. Format: addorder <atasnama> <pesanan>");
            }
        } else if (intent.equalsIgnoreCase("tambahpesanan")) {
            if (words.length >= 3) {
                int id = Integer.valueOf(words[1]);
                int total = 0;
                String fullOrder = "";
                for (int i = 2; i < words.length; i++) {
                    if (isOnTheMenu(words[i]) != null) {
                        Menu m = isOnTheMenu(words[i]);
                        fullOrder += m.getNama() + " ";
                        total += m.getHarga();
                        if (i != words.length - 1) {
                            fullOrder += " ";
                        }
                    } else {
                        pushMessage(aUserId, words[i] + " tidak ada di menu dan tidak ditambahkan ke pesanan");
                    }
                }
                if (!fullOrder.equals("")) {
                    Boolean test = updateOrder(id, fullOrder, total);
                    Order o = findOrderDetail(id);
                    String message = fullOrder + " telah ditambahkan ke pesanan dengan ID: " + id +
                            "\nTotal menjadi: Rp." + o.getTotal();
                    pushMessage(aUserId, message);
                    if (test) {
                        pushMessage(aUserId, "Pesanan berhasil diupdate");
                    }
                } else {
                    pushMessage(aUserId, "Format pesanan salah. Format: tambahpesanan <id> <pesanan>");
                }
            } else {
                pushMessage(aUserId, "Format pesanan salah. Format: tambahpesanan <id> <pesanan>");
            }
        } else if (intent.equalsIgnoreCase("deleteorder")) {
            if (words.length > 1) {
                int id = Integer.valueOf(words[1]);
                Order o = findOrderDetail(id);
                if (o.getHutang() != 0) {
                    Boolean test = deleteOrder(id);
                    if (test) {
                        pushMessage(aUserId, "Pesanan berhasil dihapus");
                        System.out.println("Berhasil");
                    } else {
                        pushMessage(aUserId, "Pesanan gagal dihapus");
                        System.out.println("Gagal");
                    }
                } else {
                    pushMessage(aUserId, "Pesanan yang sudah lunas tidak dapat dihapus.");
                }
            } else {
                pushMessage(aUserId, "Format delete salah. Deleteorder <id>");
            }
        } else if (intent.equalsIgnoreCase("orderid")) {
            if (words.length > 1) {
                int id = Integer.valueOf(words[1]);
                System.out.println("Order id: " + id);
                Order o = findOrderDetail(id);
                System.out.println(o.display());
                if (o.getId() != 0) {
                    String Msg = "";
                    Msg += o.display();
                    if (o.getHutang() > 0) {
                        Msg += "\nStatus: Belum lunas\nHutang: " + o.getHutang();
                    } else
                        Msg += "\nStatus: Lunas";
                    pushMessage(aUserId, Msg);
                    System.out.println(Msg);
                } else {
                    pushMessage(aUserId, "Pesanan tidak ditemukan");
                    System.out.println("Tidak ditemukan");
                }
            } else {
                pushMessage(aUserId, "Format orderid salah. Orderid <id>");
            }
        } else if (intent.equalsIgnoreCase("bayar")) {
            if (words.length != 1) {
                int id = Integer.valueOf(words[1]);
                Order o = findOrderDetail(id);
                if (o.getNama() != null) {
                    if (o.getHutang() != 0) {
                        System.out.println(words[1]);
                        System.out.println(words.length);
                        int jumlah;
                        if (words.length < 3) {
                            jumlah = o.getHutang();
                            Boolean test = bayarOrder(id, jumlah);
                            if (test) {
                                pushMessage(aUserId, "Pesanan dengan ID: " + id + " sekarang sudah lunas");
                            } else
                                pushMessage(aUserId, "Gagal mengupdate total");
                        } else {
                            jumlah = Integer.valueOf(words[2]);
                            if (jumlah <= o.getHutang()) {
                                Boolean test = bayarOrder(id, jumlah);
                                if (test)
                                    pushMessage(aUserId, "Pesanan dengan ID:" + id + " telah membayar Rp." + jumlah + "\nSisa hutang: Rp." + (o.getHutang() - jumlah));
                            } else
                                pushMessage(aUserId, "Jumlah bayar melebihi hutang. Hutang: Rp." + o.getHutang());
                        }
                    } else {
                        pushMessage(aUserId, "Pesanan ID: " + id + " tidak memiliki hutang dan sudah lunas");
                    }
                } else {
                    pushMessage(aUserId, "Pesanan tidak ditemukan");
                }
            } else {
                pushMessage(aUserId, "Format bayar salah: bayar <id>\nAtau\nbayar <id> <jumlah>");
            }
        } else if (intent.equalsIgnoreCase("totalorder")) {
            if (words.length < 2) {
                if (getTotalOrderInfo(tanggal)[0] != 0) {
                    pushMessage(aUserId, "Total order hari ini: " + getTotalOrderInfo(tanggal)[0]
                            + "\nJumlah pemasukan: " + (getTotalOrderInfo(tanggal)[1] - getTotalOrderInfo(tanggal)[2])
                            + "\nJumlah hutang: " + getTotalOrderInfo(tanggal)[2]);
                } else {
                    pushMessage(aUserId, "Belum ada order hari ini");
                }
            } else {
                String hari = words[1];
                if (getTotalOrderInfo(hari)[0] != 0) {
                    pushMessage(aUserId, "Total order pada tanggal " + hari + ": " + getTotalOrderInfo(hari)[0]
                            + "\nJumlah pemasukan: " + (getTotalOrderInfo(hari)[1] - getTotalOrderInfo(hari)[2])
                            + "\nJumlah hutang: " + getTotalOrderInfo(hari)[2]);
                } else {
                    pushMessage(aUserId, "Tidak ada order pada tanggal " + hari);
                }
            }
        } else if (intent.equalsIgnoreCase("orderhariini")) {
            pushMessage(aUserId, getOrder(tanggal));
        } else if (intent.equalsIgnoreCase("ordertanggal")) {
            if (words.length != 1) {
                String hari = words[1];
                pushMessage(aUserId, getOrder(hari));
            } else {
                pushMessage(aUserId, "Format salah: ordertanggal <tanggal>\nGunakan format tanggal: hh-bb-tttt");
            }
        } else if (intent.equalsIgnoreCase("hutang")) {
            pushMessage(aUserId, getTotalHutang());
        } else if (intent.equalsIgnoreCase("pjdanus")) {
            if (words.length > 1) {
                System.out.println("processed.");
                String hari = words[1];
                if (isValidHari(hari, day))
                    pushMessage(aUserId, getPJDanus(hari));
                else
                    pushMessage(aUserId, "Hari yang dimasukkan salah");
            } else
                pushMessage(aUserId, getPJDanus("semua"));
        } else if (intent.equalsIgnoreCase("addpjdanus")) {
            if (words.length != 1) {
                String mode = words[1];
                if (words.length != 2 && words.length <= 4) {
                    String hari = words[2];
                    String nama = words[3];
                    nama = firstLetterUpperCase(nama);
                    Boolean test = updatePJDanus(hari, nama, mode);
                    if (test)
                        pushMessage(aUserId, nama + " telah ditambahkan ke hari " + hari + " sebagai PJ " + mode);
                    else
                        pushMessage(aUserId, "Gagal, format salah atau error di query. Hubungi Abyan");
                } else {
                    pushMessage(aUserId, "Format salah. Format: addpjdanus <danus/mesen> <hari> <nama>");
                }
            } else {
                pushMessage(aUserId, "Format salah. Format: addpjdanus <danus/mesen> <hari> <nama>");
            }
        } else if (intent.equalsIgnoreCase("deletepjdanus")) {
            if (words.length != 1) {
                String mode = words[1];
                if (words.length != 2 && words.length <= 4) {
                    String hari = words[2];
                    String nama = words[3];
                    nama = firstLetterUpperCase(nama);
                    Boolean test = deletePJDanus(hari, nama, mode);
                    if (test)
                        pushMessage(aUserId, nama + " (PJ " + mode + ") telah dihapus dari hari " + hari);
                    else
                        pushMessage(aUserId, "Gagal, format salah atau error di query. Hubungi Abyan");
                } else {
                    pushMessage(aUserId, "Format salah. Format: deletepjdanus <danus/mesen> <hari> <nama>");
                }
            } else {
                pushMessage(aUserId, "Format salah. Format: deletepjdanus <danus/mesen> <hari> <nama>");
            }
        } else if (intent.equalsIgnoreCase("listmenu")) {
            pushMessage(aUserId, getMenuList());
        } else if (intent.equalsIgnoreCase("addmenu")) {
            if (words.length != 1) {
                String nama = words[1];
                firstLetterUpperCase(nama);
                int harga = Integer.valueOf(words[2]);
                String kategori = words[3];
                if (kategori.equalsIgnoreCase("makanan") || kategori.equalsIgnoreCase("minuman")) {
                    Boolean execute = updateMenu(nama, harga, kategori);
                    if (execute) {
                        pushMessage(aUserId, nama + " dengan harga " + harga + " (" + kategori + ") telah ditambahkan ke dalam menu list");
                    } else {
                        pushMessage(aUserId, "Gagal, format salah atau error di query. Hubungi Abyan");
                    }
                } else {
                    pushMessage(aUserId, "Kategori salah. Kategori yang tersedia: 'makanan' dan 'minuman'");
                }
            } else {
                pushMessage(aUserId, "Format salah. Format: addmenu <nama> <harga> <id_kategori>");
            }
        } else if (intent.equalsIgnoreCase("deletemenu")) {
            if (words.length != 1) {
                String nama = words[1];
                Boolean execute = deleteMenu(nama);
                if (execute) {
                    pushMessage(aUserId, nama + " berhasil dihapus dari list menu");
                } else {
                    pushMessage(aUserId, "Gagal, format salah atau menu yang akan didelete tidak ada didalam list.");
                }
            } else {
                pushMessage(aUserId, "Format salah. Format: deletemenu <nama>");
            }
        } else if (intent.equalsIgnoreCase("pjwarkop")) {
            if (words.length > 1) {
                System.out.println("processed.");
                String hari = words[1];
                if (isValidHari(hari, day))
                    pushMessage(aUserId, getPJWarkop(hari));
                else
                    pushMessage(aUserId, "Hari yang dimasukkan salah");
            } else
                pushMessage(aUserId, getPJWarkop("semua"));
        } else if (intent.equalsIgnoreCase("addpjwarkop")) {
            if (words.length >= 1 && words.length <= 4) {
                String hari = words[1];
                String nama = words[2];
                int sesi;
                if (words.length > 2)
                    sesi = Integer.valueOf(words[3]);
                else
                    sesi = 0;
                nama = firstLetterUpperCase(nama);
                Boolean execute = addPJWarkop(hari, nama, sesi);
                if (execute)
                    if (sesi != 0)
                        pushMessage(aUserId, nama + " telah ditambahkan ke hari " + hari + " sebagai PJ jaga sesi " + sesi);
                    else
                        pushMessage(aUserId, nama + " telah ditambahkan ke hari " + hari + " sebagai PJ harian");
                else
                    pushMessage(aUserId, "Gagal, format salah atau error di query. Hubungi Abyan");
            } else {
                pushMessage(aUserId, "Format salah. Format: addpjwarkop <hari> <nama> <sesi>\nCat. Sesi tidak perlu diisi apabila ingin menambahkan PJ harian.");
            }
        } else if (intent.equalsIgnoreCase("deletepjwarkop")) {
            if (words.length >= 1 && words.length <= 4) {
                String hari = words[1];
                String nama = words[2];
                int sesi;
                if (words.length > 2)
                    sesi = Integer.valueOf(words[3]);
                else
                    sesi = 0;
                nama = firstLetterUpperCase(nama);
                Boolean execute = delPJWarkop(hari, nama, sesi);
                if (execute)
                    if (sesi != 0)
                        pushMessage(aUserId, nama + " (PJ jaga sesi " + sesi + ") telah dihapus dari hari " + hari);
                    else
                        pushMessage(aUserId, nama + " (PJ harian) telah dihapus dari hari " + hari);
                else
                    pushMessage(aUserId, "Gagal, format salah atau error di query. Hubungi Abyan");
            } else {
                pushMessage(aUserId, "Format salah. Format: deletepjwarkop <hari> <nama> <sesi>\nCat. Sesi tidak perlu diisi apabila ingin menghapus PJ harian.");
            }
        }
    }

    private Boolean regOrder(String nama, String pemesanan, int total, String tanggal, String jam) {
        int i = mDatabase.registerOrder(nama, pemesanan, total, tanggal, jam);
        return i == 1;

    }

    private Boolean deleteOrder(int id) {
        int i = mDatabase.deleteOrder(id);
        return i == 1;
    }

    private Boolean bayarOrder(int orderid, int jumlah) {
        int i = mDatabase.bayarOrder(orderid, jumlah);
        return i == 1;
    }

    private Boolean updateOrder(int id, String pesanan, int harga) {
        int i = mDatabase.updateOrder(id, pesanan, harga);
        return i == 1;
    }

    private Boolean updatePJDanus(String hari, String nama, String mode) {
        int i = mDatabase.updatePJ(hari, nama, mode);
        return i == 1;
    }

    private Boolean deletePJDanus(String hari, String nama, String mode) {
        int i = mDatabase.deletePJ(hari, nama, mode);
        return i == 1;
    }

    private Boolean addPJWarkop(String hari, String nama, int sesi) {
        int i = mDatabase.updatePJWarkop(hari, nama, sesi);
        return i == 1;
    }

    private Boolean delPJWarkop(String hari, String nama, int sesi) {
        int i = mDatabase.deletePJWarkop(hari, nama, sesi);
        return i == 1;
    }

    private int getLatestID() {
        Order o = mDatabase.getLatestID();
        return o.getId();
    }

    private Order findOrderDetail(int orderID) {
        Order o = new Order();
        List<Order> order = mDatabase.getByOrderID(orderID);
        if (order.size() > 0) {
            o = order.get(0);
        }
        return o;
    }

    private Integer[] getTotalOrderInfo(String tanggal) {
        return mDatabase.getTotalOrder(tanggal);
    }

    private String getMenuList() {
        String Msg = "";
        List<Menu> m = mDatabase.getListMenuMakanan();
        Menu M;
        if (m.size() > 0) {
            Msg+= "=== Makanan ===\n";
            for (int i = 0; i < m.size(); i++) {
                M = m.get(i);
                String nama = firstLetterUpperCase(M.getNama());
                Msg += nama + " | Harga: " + M.getHarga();
                if (i != m.size() - 1)
                    Msg += "\n";
            }
        }
        m = mDatabase.getListMenuMinuman();
        if (m.size() > 0) {
            Msg+= "\n\n=== Minuman ===\n";
            for (int i = 0; i < m.size(); i++) {
                M = m.get(i);
                String nama = firstLetterUpperCase(M.getNama());
                Msg += nama + " | Harga: " + M.getHarga();
                if (i != m.size() - 1)
                    Msg += "\n";
            }
        }
        return Msg;
    }

    private String getTotalHutang() {
        String Msg = "";
        List<Order> o = mDatabase.getHutang();
        if (o.size() > 0) {
            for (Order a : o) {
                Msg += "ID Pesanan: " + a.getId() + " | Nama: " + a.getNama() + " | Rp." + a.getHutang() + "\n";
            }
        } else
            Msg = "Tidak ada hutang";
        return Msg;
    }

    private String getPJDanus(String hari) {
        String Msg = "";
        List<PJDanus> Pjd;
        if (hari.equals("semua")) {
            Pjd = mDatabase.getPJDanus("semua");
        } else {
            Pjd = mDatabase.getPJDanus(hari);
        }
        if (Pjd.size() > 0) {
            for (int i = 0; i < Pjd.size(); i++) {
                PJDanus a = Pjd.get(i);
                String nd[] = a.getListNamaDagang().split("\\s");
                String np[] = a.getListNamaPesen().split("\\s");
                Msg += "==" + a.getHari() + "==" + "\n =PJ Danus= \n";
                for (String aNd : nd) {
                    Msg += "- " + aNd + "\n";
                }
                Msg += "\n =PJ Mesen= \n";
                for (String aNp : np) {
                    Msg += "- " + aNp + "\n";
                }
                if (i != Pjd.size() - 1)
                    Msg += "\n";
            }
        }
        return Msg;
    }

    private String getPJWarkop(String hari) {
        String Msg = "";
        List<PJWarkop> Pjw;
        if (hari.equals("semua")) {
            Pjw = mDatabase.getPJWarkop("semua");
        } else {
            Pjw = mDatabase.getPJWarkop(hari);
        }
        if (Pjw.size() > 0) {
            for (int i = 0; i < Pjw.size(); i++) {
                PJWarkop a = Pjw.get(i);
                String nd[] = a.getNama().split("\\s");
                String sesi1[] = a.getSesi1().split("\\s");
                String sesi2[] = a.getSesi2().split("\\s");
                String sesi3[] = a.getSesi3().split("\\s");
                Msg += "===" + a.getHari() + "===" + "\n =PJ Warkop= \n";
                for (String aNd : nd) {
                    Msg += "- " + aNd + "\n";
                }
                Msg += "\n =PJ Sesi= \nSesi 1:\n";
                for (String aSesi1 : sesi1) {
                    Msg += "- " + aSesi1 + "\n";
                }
                Msg += "\nSesi 2:\n";
                for (String aSesi2 : sesi2) {
                    Msg += "- " + aSesi2 + "\n";
                }
                Msg += "\nSesi 3:\n";
                for (String aSesi3 : sesi3) {
                    Msg += "- " + aSesi3+ "\n";
                }
                if (i != Pjw.size() - 1)
                    Msg += "\n";
            }
            Msg += "\nSesi 1: 06:00 - 12:00\nSesi 2: 12:00 - 18:00\nSesi 3: 18:00 - 23:00";
        }
        return Msg;
    }

    private String getOrder(String tanggal) {
        String Msg = "";
        List<Order> o = mDatabase.getOrder(tanggal);
        if (o.size() > 0) {
            for (int i = 0; i < o.size(); i++) {
                Order a = o.get(i);
                Msg += "ID Pesanan: " + a.getId() + " | Nama: " + a.getNama() + "\nPesanan: " + a.getPesanan() + " | Total: " + a.getTotal();
                if (a.getHutang() == 0) {
                    Msg += " | Status: Lunas";

                } else {
                    Msg += " | Status: Belum lunas";
                }
                if (i != o.size() - 1)
                    Msg += "\n";
            }
        } else
            Msg = "Belum ada order hari ini";
        return Msg;
    }

    public Menu isOnTheMenu(String nama) {
        Menu m = mDatabase.getMenuDetail(nama);
        if (m != null) {
            System.out.println(m.getNama() + " dengan harga " + m.getHarga() + " kategori: " + m.getKategori() + " loaded.");
        }
        return m;
    }

    private Boolean updateMenu(String nama, int harga, String kategori) {
        int i = mDatabase.updateMenu(nama, harga, kategori);
        return i == 1;
    }

    private Boolean deleteMenu(String nama) {
        int i = mDatabase.deleteMenu(nama);
        return i == 1;
    }

    private static boolean isValidHari(String hari, String[] d) {
        for (String aD : d) {
            if (hari.equalsIgnoreCase(aD)) {
                return true;
            }
        }
        return false;
    }

    private static int getPriceByName(String[][] menu, String name) {

        int k = 0;
        for (int i = 0; i < menu.length; i++) {
            if (menu[i][0].equalsIgnoreCase(name)) {
                k = i;
            }
        }
        return k;
    }

    private static Boolean isCmdText(String[] cmd, String input) {
        for (String aCmd : cmd) {
            if (input.contains(aCmd)) {
                System.out.println(input);
                return true;
            }
        }
        return false;
    }

    private static String firstLetterUpperCase(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private static String getRandomJawaban(int i, String[][] array) {
        String[] count = array[i][1].replace("[", "").replace("]", "").split(",");
        System.out.println(array[i][1]);
        String random;
        int rnd = new Random().nextInt(count.length);
        if (rnd != 1) {
            System.out.println(rnd);
            System.out.println("Tidak sama dengan satu");
            random = (count[rnd]);
        } else {
            System.out.println("Sama dengan satu");
            random = (count[2]);
        }
        return random;
    }
}