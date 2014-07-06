package pt.inesc.ask;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.google.common.io.BaseEncoding;


public class Test {

    public static void main(String[] args) throws UnsupportedEncodingException {
        String s = "Complete+Dropbox+implementation+for+Androidcrawler&lt;p&gt;Titanium Media Sync offers continuous sync from SD Card &lt;em&gt;to&lt;/em&gt; DropBox, but not the other way... the way I do it is setup rsync jobs with tasker and rsyncbackup... (requires your DropBox to be automatically kept in sync on a desktop pc somewhere, running an ssh server). The following should get you up and running:&lt;/p&gt;&#xA;&#xA;&lt;h1&gt;Step 1: DropBox remote.&lt;/h1&gt;&#xA;&#xA;&lt;p&gt;First, setup DropBox on a computer somewhere running an ssh server, so you can copy files from it over ssh (with rsync), and make sure it?s setup with private key encryption with an agent so you don?t need to enter passphrases/passwords every time&lt;/p&gt;&#xA;&#xA;&lt;h1&gt;Step 2: RSync from remote to phone&lt;/h1&gt;&#xA;&#xA;&lt;p&gt;Then, get rsync4android (&lt;a href=&quot;https://market.android.com/details?id=eu.kowalczuk.rsync4android&quot;&gt;market link&lt;/a&gt;)&#xA;That lets you setup rsync jobs from a remote server to your phone (and vice versa? I use it for this, but also for backing up my photos to my PC automatically every night)&#xA;Then create an rsync job to download a particular folder from your remote DropBox on your PC into the DropBox folder on your sd card.&lt;/p&gt;&#xA;&#xA;&lt;h1&gt;Step 3: scheduled automatic rsync jobs&lt;/h1&gt;&#xA;&#xA;&lt;p&gt;Get tasker (which lets you schedule jobs to happen on a large number of triggers, including time) (&lt;a href=&quot;https://market.android.com/details?id=net.dinglisch.android.taskerm&amp;amp;feature=search_result&quot;&gt;market link&lt;/a&gt;)&#xA;Create a tasker job to fire off your new rsync job to keep your dropbox folder up to date.&lt;/p&gt;&#xA;&#xA;&lt;h1&gt;Step 4: Enjoy&lt;/h1&gt;&#xA;&#xA;&lt;p&gt;Like I say, it?s a bit around the houses, but seems to do the job&lt;/p&gt;&#xA;&#xA;&lt;p&gt;I've also posted a copy of these instructions &lt;a href=&quot;http://seb.so/continuous-android-sync-optionally-with-dropbox/&quot;&gt;on my blog&lt;/a&gt;.&lt;/p&gt;&#xA;";
        System.out.println(s);
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(s.getBytes("UTF-16"));
            String msg = BaseEncoding.base64().encode(digest);
            for (int i = 0; i < digest.length; i++)
                System.out.println(digest[i]);
            System.out.println(msg);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
