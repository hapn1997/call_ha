package com.huyanh.base.dao;


import android.content.Context;

import com.huyanh.base.utils.BaseUtils;
import com.huyanh.base.utils.Log;

import java.io.Serializable;
import java.util.ArrayList;

public class BaseConfig {
    private Update update = new Update();

    private key key = new key();

    private config_ads config_ads = new config_ads();

    private ads_network_new ads_network_new = new ads_network_new();

    private thumnail_config thumnail_config = new thumnail_config();
    private ArrayList<more_apps> more_apps = new ArrayList<>();
    private ArrayList<shortcut_dynamic> shortcut_dynamic = new ArrayList<>();

    public void initOtherApps(Context context) {
        try {
            int i = 0;
            while (i < more_apps.size()) {
                if (BaseUtils.isInstalled(context, BaseConfig.this.more_apps.get(i).getPackagename())) {
                    BaseConfig.this.more_apps.remove(i);
                } else {
                    i++;
                }
            }


            i = 0;
            while (i < shortcut_dynamic.size()) {
                String listPackageName[];
                boolean isInstalled = false;
                if (shortcut_dynamic.get(i).getPackage_name().contains(",")) {
                    listPackageName = shortcut_dynamic.get(i).getPackage_name().split(",");
                } else {
                    listPackageName = new String[]{shortcut_dynamic.get(i).getPackage_name()};
                }
                for (String packageName : listPackageName) {
                    if (BaseUtils.isInstalled(context, packageName)) {
                        isInstalled = true;
                        break;
                    }
                }
                if (isInstalled) {
                    BaseConfig.this.shortcut_dynamic.remove(i);
                } else {
                    i++;
                }
            }
        } catch (Exception e) {
            Log.e("error initOtherApps: " + e.getMessage());
        }

    }

    public ArrayList<shortcut_dynamic> getShortcut_dynamic() {
        return shortcut_dynamic;
    }

    public Update getUpdate() {
        return update;
    }

    public BaseConfig.key getKey() {
        return key;
    }

    public BaseConfig.config_ads getConfig_ads() {
        return config_ads;
    }

    public BaseConfig.ads_network_new getAds_network_new() {
        return ads_network_new;
    }

    public BaseConfig.thumnail_config getThumnail_config() {
        return thumnail_config;
    }

    public ArrayList<BaseConfig.more_apps> getMore_apps() {
        return more_apps;
    }

    public class Update {
        int status = 0;
        int offset_show = 0;
        String title = "";
        String description = "";
        String url_store = "";
        String type = "";
        String version = "";
        String packagename = "";

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getOffset_show() {
            return offset_show;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getUrl_store() {
            return url_store;
        }

        public void setUrl_store(String url_store) {
            this.url_store = url_store;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getPackagename() {
            return packagename;
        }
    }

    public class key {
        admob admob = new admob();
        facebook facebook = new facebook();

        public key.admob getAdmob() {
            return admob;
        }

        public key.facebook getFacebook() {
            return facebook;
        }

        public class admob {
            String appid = "ca-app-pub-3602251130565338~5768146603";
            String banner = "ca-app-pub-9849209325477495/6152469966";
            String popup = "ca-app-pub-9849209325477495/9105936362";

            public String getAppid() {
                return appid;
            }

            public String getBanner() {
                return banner;
            }

            public String getPopup() {
                return popup;
            }
        }

        public class facebook {
            String banner = "999215223462174_1209853995731628";
            String popup = "999215223462174_1174872629229765";
            String thumbai = "999215223462174_1209854065731621";

            public String getPopup() {
                return popup;
            }

            public String getBanner() {
                return banner;
            }

            public String getThumbai() {
                return thumbai;
            }
        }
    }

    public class config_ads {
        int time_start_show_popup = 15;
        int offset_time_show_popup = 150;
        int time_hidden_to_click_banner = 0;
        int time_hidden_to_click_popup = 0;

        public int getOffset_time_show_popup() {
            return offset_time_show_popup;
        }

        public void setOffset_time_show_popup(int offset_time_show_popup) {
            this.offset_time_show_popup = offset_time_show_popup;
        }

        public int getTime_hidden_to_click_banner() {
            return time_hidden_to_click_banner;
        }

        public int getTime_hidden_to_click_popup() {
            return time_hidden_to_click_popup;
        }

        public int getTime_start_show_popup() {
            return time_start_show_popup;
        }

        public void setTime_start_show_popup(int time_start_show_popup) {
            this.time_start_show_popup = time_start_show_popup;
        }
    }

    public class ads_network_new {
        String banner = "admob";
        String popup = "admob";
        String thumbai = "admob";

        public String getBanner() {
            return banner;
        }

        public String getPopup() {
            return popup;
        }

        public String getThumbai() {
            return thumbai;
        }
    }

    public class thumnail_config {
        int start_video_show_thumbai = 6;
        int offset_video_to_show_thumbai = 6;
        int random_show_thumbai_hdv = 0;
        int random_show_thumbai_detail_hdv = 50;
        int start_show_thumbai_detail_news = 4;
        int offset_show_thumbai_detail_news = 10;
        int random_show_popup_hdv = 20;
        int offset_show_thumbai_end_detail_news = 8;

        public int getRandom_show_popup_hdv() {
            return random_show_popup_hdv;
        }

        public int getOffset_video_to_show_thumbai() {
            if (offset_video_to_show_thumbai == 0) return 6;
            return offset_video_to_show_thumbai;
        }

        public void setOffset_video_to_show_thumbai(int offset_video_to_show_thumbai) {
            this.offset_video_to_show_thumbai = offset_video_to_show_thumbai;
        }

        public int getRandom_show_thumbai_hdv() {
            return random_show_thumbai_hdv;
        }

        public int getRandom_show_thumbai_detail_hdv() {
            return random_show_thumbai_detail_hdv;
        }

        public int getStart_video_show_thumbai() {
            return start_video_show_thumbai;
        }

        public void setStart_video_show_thumbai(int start_video_show_thumbai) {
            this.start_video_show_thumbai = start_video_show_thumbai;
        }

        public int getOffset_show_thumbai_detail_news() {
            if (offset_show_thumbai_detail_news == 0) return 10;
            return offset_show_thumbai_detail_news;
        }

        public int getStart_show_thumbai_detail_news() {
            if (start_show_thumbai_detail_news == 0) return 4;
            return start_show_thumbai_detail_news;
        }

        public int getOffset_show_thumbai_end_detail_news() {
            if (offset_show_thumbai_end_detail_news == 0) return 8;
            return offset_show_thumbai_end_detail_news;
        }

        public void setRandom_show_popup_hdv(int random_show_popup_hdv) {
            this.random_show_popup_hdv = random_show_popup_hdv;
        }

        public void setRandom_show_thumbai_hdv(int random_show_thumbai_hdv) {
            this.random_show_thumbai_hdv = random_show_thumbai_hdv;
        }
    }

    public class more_apps implements Serializable {
        String title = "";
        String description = "";
        String packagename = "";
        String type = "";
        String icon = "";
        String url_store = "";
        String banner = "";
        String thumbai = "";

        String popup = "";

        public String getPopup() {
            if (popup == null || popup.equals(""))
                return thumbai;

            return popup;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getPackagename() {
            return packagename;
        }

        public String getType() {
            return type;
        }

        public String getIcon() {
            return icon;
        }

        public String getUrl_store() {
            return url_store;
        }

        public String getBanner() {
            return banner;
        }

        public String getThumbai() {
            return thumbai;
        }
    }

    public class shortcut_dynamic {
        int id = -1;
        String icon = "", name_shotcut = "", link = "", package_name = "";
        int loop = 0;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getName_shotcut() {
            return name_shotcut;
        }

        public void setName_shotcut(String name_shotcut) {
            this.name_shotcut = name_shotcut;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getPackage_name() {
            return package_name;
        }

        public void setPackage_name(String package_name) {
            this.package_name = package_name;
        }

        public int getLoop() {
            return loop;
        }
    }
}
