package com.arenalocastro.videomanagement.services;

import com.arenalocastro.videomanagement.exceptions.InternalException;
import com.arenalocastro.videomanagement.exceptions.VideoNotFoundException;
import com.arenalocastro.videomanagement.models.Video;
import com.arenalocastro.videomanagement.models.VideoStatus;
import org.bson.types.ObjectId;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

@Service
public class CloudEdgeService {
    @Value("#{new Boolean('${IS_CLOUD:true}')}")
    private Boolean isCloud;

    @Value(value="${CLOUD_URL:}")
    private String cloudUrl;

    @Value("#{new Integer('${VARIANT_TYPE:-1}')}")
    private Integer variantType;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value(value="${KAFKA_MAIN_TOPIC}")
    private String maintopic;

    @Autowired
    private AsyncService asyncService;

    @Autowired
    private VideoService videoService;

    public Boolean isCloudAndOnlineEncoding() {
        return this.isCloud && this.variantType == 2;
    }

    private ConcurrentSkipListSet<String> processingMap = new ConcurrentSkipListSet<String>();

    private ResponseEntity<String> makeRedirectResponse(String id) {
        HttpHeaders httpHeaders = new HttpHeaders();
        try {
            httpHeaders.setLocation(new URI(cloudUrl + "/vms/videos/" + id));
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new InternalException("Error in URI syntax");
        }
        return new ResponseEntity<>(httpHeaders, HttpStatus.MOVED_TEMPORARILY);
    }

    public ResponseEntity<String> handleNotFoundVideo(String id) {
        System.out.println("IsCloud? " + isCloud);
        if (isCloud) {
            throw new VideoNotFoundException(id);
        }
        asyncService.run(postResponseEdgeHandler(id));
        return makeRedirectResponse(id);
    }

    private Runnable postResponseEdgeHandler(String id) {
        return () -> {
            System.out.println("Post GET response edge handler");
            if (processingMap.contains(id)) {
                System.out.println("Already processing");
                return;
            }
            processingMap.add(id);
            try {
                switch(variantType) {
                    case 0: // (Just a cache) Download all variants from the cloud
                        System.out.println("Cache variant: downloading all representations");
                        downloadAllVideoRepresentations(id);
                        addVideoToDatabase(id, false);
                        break;
                    case 1: // (Offline encoding in the edge) Only download the raw video from the cloud, then process it
                        System.out.println("Encode onto edge variant: downloading raw video");
                        downloadRawVideo(id);
                        System.out.println("Encode onto edge: processing raw video");
                        processVideo(id);
                        addVideoToDatabase(id, false);
                        break;
                    case 2: // (Online encoding in the edge) Only download the raw video
                        System.out.println("Online encoding variant: downloading raw video");
                        downloadRawVideo(id);
                        addVideoToDatabase(id, true);
                        break;
                    default:
                        return;
                }
            } catch(Exception e) {
                System.out.println("Exception on PostResponse Edge handler for " + id);
                System.out.println(e);
            } finally {
                processingMap.remove(id);
            }
            System.out.println("asd" + id);
        };
    }

    private void processVideo(String id) {
        if (kafkaTemplate != null)
            kafkaTemplate.send(maintopic, "process|" + id);
    }

    public void onlineEncodeVideo(String id) {
        if (kafkaTemplate != null)
            kafkaTemplate.send(maintopic, "online-encode|" + id);
    }

    private void addVideoToDatabase(String id, Boolean onlineEncoding) {
        System.out.println("Adding video to database");
        Video v = new Video("Lorem", "Lorem author"); // TODO get from cloud
        v.set_id(new ObjectId(id));
        v.setStatus(onlineEncoding ? VideoStatus.AvailableWithOnlineEncoding : VideoStatus.Available);
        System.out.println(v.get_id_string());
        videoService.saveVideo(v).block();
    }

    private void downloadAllVideoRepresentations(String id) throws IOException {
        System.out.println(cloudUrl);
        URL url = new URL(cloudUrl + "/videofiles/" + id + ".tar");
        downloadFile(url, getTarFileObj(id));
        Archiver archiver = ArchiverFactory.createArchiver("tar");
        archiver.extract(getTarFileObj(id), makeEncodedDir(id));
    }

    private void downloadRawVideo(String id) {
        try {
            URL url = new URL(cloudUrl + "/videoraw/" + id + "/video.mp4");
            downloadFile(url, makeRawFile(id));
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }
    private void downloadFile(URL url, File dest) {
        try(
                ReadableByteChannel rbc = Channels.newChannel(url.openStream());
                FileOutputStream fos = new FileOutputStream(dest);
        ){
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    private File makeRawFile(String id) {
        String realPath = "/video/" + id;
        File f = new File(realPath);
        if(!f.exists())
            f.mkdirs();
        realPath += "/video.mp4";
        return new File(realPath);
    }

    private File makeEncodedDir(String id) {
        String realPath = "/videofiles/";
        File f = new File(realPath);
        if(!f.exists())
            f.mkdirs();
        return f;
    }

    private File getTarFileObj(String id) {
        String realPath = "/tmp/";
        File f = new File(realPath);
        if(!f.exists())
            f.mkdirs();
        realPath += id + ".tar";
        return new File(realPath);
    }
}
