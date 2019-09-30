package com.andrew.prototype.Utils;

import android.content.Context;
import android.graphics.BitmapFactory;

import com.andrew.prototype.Adapter.ImagePickerAdapter;
import com.andrew.prototype.Model.ForumThread;
import com.andrew.prototype.Model.ImagePicker;
import com.andrew.prototype.Model.ProfileModel;
import com.andrew.prototype.Model.Report;
import com.andrew.prototype.Model.SyncImg;
import com.andrew.prototype.R;

import java.util.ArrayList;
import java.util.List;

public class Constant {
    public static final long DELAY = 3000;
    public static final long DELAY_THREAD = 1000;
    public static final int PERMISSION_READ_GALLERY_EXTERNAL = 100;
    public static final int PERMISSION_WRITE_EXTERNAL = 101;
    public static final int PERMISSION_CAMERA_TAKER = 102;
    public static final int PERMISSION_READ_FILE_EXTERNAL = 103;
    public static final int ACTIVITY_CHOOSE_IMAGE = 104;
    public static final int ACTIVITY_CHOOSE_FILE = 105;
    public static final int ACTIVITY_TAKE_IMAGE = 106;
    public static final String DB_REFERENCE_TRANSACTION_REQUEST_PROMO = "transaction_request_promo";
    public static final String DB_REFERENCE_MERCHANT_PROFILE = "merchant_profile";
    public static final String DB_REFERENCE_MERCHANT_STORY = "merchant_story";
    public static final String DB_REFERENCE_FORUM = "forum";
    public static final String DB_REFERENCE_FORUM_REPLY = "forum_reply";
    public static final String DB_REFERENCE_FORUM_IMAGE = "forum_image";
    public static final String DB_REFERENCE_FORUM_IMAGE_REPLY = "forum_image_reply";
    public static final int MAX_ALPHA = 220;

//    private static final String[] trendingTitle = {"Potret Asli Afrika Vs Anggapan Banyak Orang"
//            , "Hacker Dalam dan Luar Negeri Ramai-ramai Serang Sistem KPU"
//            , "CERDASKAN MASYARAKAT SIMALUNGUN DENGAN CARA MENULIS"
//            , "Babak Baru 'Drama' Subkhan Brebes"
//            , "WhatsApp Siapkan Fitur Baru Ini Tuk Cegah Hoax Lewat Gambar"
//            , "Perkembangan Bayi Tiap Bulan, penting untuk dibaca !!! "
//            , "Viral 52 WargaPonorogo Jual Rmh&Harta krn Isu Kiamat Pindah keMalang dsbt bkl selamat "
//            , "Bukti Baru Ini Mendorong Boeing Larang Terbang Semua Pesawat 737 MAX "
//            , "NGERI! Seorang Pria Mengamuk Tusuk Penumpang Transjakarta di Halte BKN "
//            , "Puluhan Krat Miras Disita Satpol PP "
//            , "Karena Pujangga Itu Lahirlah Aku "
//            , "Captain Marvel, Berani Beda! "
//            , "7 Kebiasaan Calon Imam Keluarga Yang Harus Dipertimbangkan "
//            , "Kenali Jenis Penyakit Satu Ini! "
//            , "AirAsia cabut dari Traveloka, media asing cium aroma persaingan tak sehat"
//            , "Bukti Baru Ini Membuat Programmer Tercengang"
//            , "Usai Bom Meledak, Ditemukan 30 Kg Bahan Peledak di Rumah Warga Lainnya "
//            , "[ULASAN] Garena Speed Drifters, Tancap Gas Terus! "
//            , "Nagih Setelah 2 Kali Berhasil Curi Motor di Kampus USU, Kali ke-3 Dipergoki, Gol! "
//            , "Konsumsi Yogurt Tiap Hari, Aman Nggak Ya? "
//            , "Waspada Penyebab Sakit Gigi Kambuh Yang Sering Diabaikan! "
//            , "Jangan Minum Kopi Pas Lagi Stres Gan, Soalnya... "
//            , "9 IMBAUAN KOCAK INI BIKIN SAKIT PERUT PAS UJIAN SEKOLAH "
//    };
//
//    private static final boolean[] trendingStatus = {
//            true, true, true,
//            false, true, false, false
//            , true, true, true, true, false, true, false
//            , false, true, true, true, true, false, true
//            , false, false
//    };

    // START THREAD

    private static final String[] threadTitle = {"Potret Asli Afrika dan Anggapan Banyak Orang"
            , "Hacker Dalam dan Luar Negeri Ramai-ramai Serang Sistem KPU"
            , "CERDASKAN MASYARAKAT SIMALUNGUN DENGAN CARA MENULIS"
            , "Babak Baru 'Drama' Subkhan Brebes"
            , "WhatsApp Siapkan Fitur Baru Ini Tuk Cegah Hoax Lewat Gambar"
            , "Waspada Penyebab Sakit Gigi Kambuh Yang Sering Diabaikan! "
            , "Perkembangan Bayi Tiap Bulan, penting untuk dibaca !!! "
            , "Bukti Baru Ini Mendorong Boeing Larang Terbang Semua Pesawat 737 MAX "
            , "NGERI! Seorang Pria Mengamuk Tusuk Penumpang Transjakarta di Halte BKN "
            , "AirAsia cabut dari Traveloka, media asing cium aroma persaingan tak sehat"
            , "Bukti Baru Ini Membuat Programmer Tercengang"
            , "Usai Bom Meledak, Ditemukan 30 Kg Bahan Peledak di Rumah Warga Lainnya "
    };

    private static final String[] threadLoc = {"Bekasi", "Jakarta", "Bogor", "Surabaya"
            , "Jogjakarta", "Semarang", "Jakarta", "Bogor", "Surabaya", "Bekasi", "Semarang", "Solo"
    };

    private static final String[] threadUsername = {"Andrew Abednego Gunawan", "Spongebob", "Iron Man"
            , "Captain America", "Superman", "Andrew Abednego Gunawan", "Spongebob", "Iron Man"
            , "Captain America", "Hector", "Chaeyoung", "Yoo Jeongyeon"
    };

    private static final String[] threadDate = {
            "26/04/2019", "25/04/2019", "24/04/2019", "21/04/2019", "07/03/2019", "04/03/2019"
            , "01/03/2019", "25/02/2019", "24/02/2019", "23/02/2019", "22/02/2019", "21/02/2019"
    };

    private static final String[] threadTime = {
            "01.00", "02.00", "15.30", "12.30", "07.05", "18.00", "12.31", "15.00", "09.20"
            , "10.00", "12.01", "13.40"
    };

    private static final int[] threadLike = {
            10, 20, 30, 40, 50, 60, 12, 22, 31, 67, 32, 10
    };

    // END THREAD

    // START REPLY THREAD, ONLY IN REPLY SECTION!

    private static final int[] threadImage = {
            R.drawable.img_thread1, R.drawable.img_thread2
            , R.drawable.img_thread3, R.drawable.img_thread4
    };

    private static final String[] threadImgType = {
            ImagePickerAdapter.STATES_IMAGE, ImagePickerAdapter.STATES_IMAGE
            , ImagePickerAdapter.STATES_IMAGE, ImagePickerAdapter.STATES_IMAGE
            , ImagePickerAdapter.STATES_IMAGE
    };

    private static final String[] replyUsername = {"Ultra-man", "Patrick", "Renata"
            , "CV. Pencinta Damai", "Bear Brand", "Tupperware", "Hector"
            , "Republic of Gamer", "Stefani", "Ekho Adhitya", "Ni Wayan Bianka Aristania"
    };

    private static final String[] replyDate = {
            "26/02/2019", "27/02/2019", "28/02/2019", "01/03/2019", "02/03/2019", "03/03/2019"
            , "04/03/2019", "05/03/2019", "06/03/2019", "07/03/2019", "08/03/2019"
    };

    private static final String[] replyTime = {
            "02.05", "05.20", "12.30", "18.30", "09.05", "10.11", "13.10", "17.00", "20.00"
            , "19.20", "11.10"
    };

    private static final String[] replyLoc = {"Makasar", "Solo", "Banten", "Palembang"
            , "Jayapura", "Bekasi", "Solo", "Serpong", "Jakarta", "Lampung", "Medan"
    };

    private static final int[] replyLike = {
            5, 15, 25, 35, 45, 55, 72, 10, 12, 50, 40
    };

    private static final int[] replyProfilePicture = {
            R.drawable.img_profile1, R.drawable.img_profile2, R.drawable.img_profile3
            , R.drawable.img_profile4, R.drawable.img_profile5, R.drawable.img_profile6
            , R.drawable.img_profile7, R.drawable.img_profile8, R.drawable.img_profile9
            , R.drawable.img_profile10, R.drawable.img_profile11
    };

    private static final String[] replyNameEachProfile = {
            "Ultra-man", "Ultra-man", "Ultra-man"
            , "Ekho Adhitya", "Ekho Adhitya", "Ekho Adhitya"
            , "Renata", "Renata"
            , "CV. Pencinta Damai"
            , "Hector", "Hector", "Hector"
    };

    private static final int[] replyImageEachProfile = {
            R.drawable.img_thread1, R.drawable.img_thread4, R.drawable.img_thread2
            , R.drawable.img_thread2, R.drawable.img_thread3, R.drawable.img_thread1
            , R.drawable.img_thread4, R.drawable.img_thread2
            , R.drawable.img_thread3
            , R.drawable.img_thread1, R.drawable.img_thread2, R.drawable.img_thread3
    };

    // END REPLY THREAD

    private static final String[] reportList = {
            "Ujaran Kebencian", "Pornografi", "Berita Bohong / Palsu", "Spam"
    };

    // START SHOW CASE ASSET

    private static final int[] imageAsset = {0, R.drawable.img1, R.drawable.img2, R.drawable.img3
            , R.drawable.img4, R.drawable.img5
    };

    private static final String[] merchantName = {"", "Patrick", "Renata"
            , "CV. Pencinta Damai", "Bear Brand", "Ultra-man"
    };

    private static final String[] desc = {"", "Desc1", "Desc2", "Desc3", "Desc4", "Desc5"};

    // END SHOW CASE ASSET

    // START PROFILE FRAGMENT

    private static final int[] icon = {R.drawable.ic_group_people, R.drawable.ic_people_setting
            , R.drawable.ic_store_add, R.drawable.ic_request, R.drawable.ic_forum, R.drawable.ic_card_giftcard
            , R.drawable.ic_help_center, R.drawable.ic_phone, R.drawable.ic_logout};

    private static final String[] parent = {"Kelola Anggota", "Pengaturan Profile", "Tambah Cabang"
            , "Ajukan Promo", "Merchant Forum", "Merchant Loyalti", "Pusat Bantuan", "Tentang Aplikasi", "Keluar"};

    private static final String[] child = {"Lihat & atur anggota Anda", "Lihat & atur email dan password Anda"
            , "Ajukan penambahan cabang yang belum", "Ajukan promosi kerja sama Anda", "Diskusi dengan sesama merchant"
            , "Lihat loyalti Anda", "Lihat solusi terbaik dan hubungi Kami", "Pastikan Anda menggunakan versi terbaru"
            , "Keluar akun"};

    //END PROFILE FRAGMENT

    // START ADS IMAGE TEMPLATE

    public static final int[] iconTemplateAds = {R.drawable.ic_promo_box1, R.drawable.ic_promo_box2, R.drawable.ic_promo_box3
            , R.color.orchid_palette, R.color.vivid_violet_palette
            , R.color.fruit_salad_palette, R.color.ripe_lemon_palette, R.color.selective_yellow_palette
            , R.color.west_coast_palette, R.color.cerulean_palette, R.color.tamarillo_palette, R.drawable.ic_rainbow};

    public static final int[] imgTemplateAds = {R.drawable.img_promo_box1, R.drawable.img_promo_box2, R.drawable.img_promo_box3
            , R.color.orchid_palette, R.color.vivid_violet_palette
            , R.color.fruit_salad_palette, R.color.ripe_lemon_palette, R.color.selective_yellow_palette
            , R.color.west_coast_palette, R.color.cerulean_palette, R.color.tamarillo_palette, R.drawable.ic_rainbow};

    // END ADS

    public static List<ForumThread> getTrending(Context context) {
        List<ForumThread> forumList = new ArrayList<>();
        for (int i = 0; i < threadTitle.length; i++) {
            forumList.add(new ForumThread(threadTitle[i]
                    , threadDate[i], threadTime[i], threadUsername[i]
                    , context.getString(R.string.lorem), threadLike[i], threadLoc[i]));
        }
        return forumList;
    }

    public static List<ForumThread> getForumList(Context context) {
        List<ForumThread> forumList = new ArrayList<>();
        for (int i = 0; i < threadTitle.length; i++) {
            forumList.add(new ForumThread(threadTitle[i]
                    , threadDate[i], threadTime[i], threadUsername[i]
                    , context.getString(R.string.lorem), threadLike[i], threadLoc[i]));
        }
        return forumList;
    }

    public static List<ImagePicker> getImage() {
        List<ImagePicker> imagePickers = new ArrayList<>();
        for (int i = 0; i < threadImage.length; i++) {
            imagePickers.add(new ImagePicker(threadImage[i], threadImgType[i]));
        }
        return imagePickers;
    }

    public static List<ForumThread> getReply(Context context) {
        List<ForumThread> forumThreads = new ArrayList<>();
        for (int i = 0; i < replyDate.length; i++) {
            forumThreads.add(new ForumThread(replyDate[i], replyTime[i]
                    , context.getResources().getString(R.string.lorem), replyUsername[i]
                    , replyLoc[i], replyLike[i], replyProfilePicture[i]));
        }
        return forumThreads;
    }

    public static List<Report> getReport() {
        List<Report> strings = new ArrayList<>();
        for (String aReportList : reportList) {
            strings.add(new Report(aReportList, false));
        }
        return strings;
    }

    public static List<ProfileModel> getProfileModels() {
        List<ProfileModel> profileModels = new ArrayList<>();
        int i = 0;
        for (String parent : parent) {
            profileModels.add(new ProfileModel(parent, child[i], icon[i]));
            i++;
        }
        return profileModels;
    }

    public static List<SyncImg> getImageEachReply(Context context) {
        List<SyncImg> syncImgs = new ArrayList<>();
        int i = 0;
        for (String s : replyNameEachProfile) {
            syncImgs.add(new SyncImg(s, BitmapFactory.decodeResource(context.getResources(), replyImageEachProfile[i]), ""));
            i++;
        }
        return syncImgs;
    }
}
