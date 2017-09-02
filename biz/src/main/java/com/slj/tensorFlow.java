package com.slj;

import org.bytedeco.javacpp.tensorflow;

/**
 * Created by slj on 17/2/23.
 */
public class tensorFlow {

    public static void main(String[] args) {
        final tensorflow.Session session = new tensorflow.Session(new tensorflow.SessionOptions());
        tensorflow.GraphDef def = new tensorflow.GraphDef();
        tensorflow.ReadBinaryProto(tensorflow.Env.Default(),
                "somedir/trained_model.proto", def);
        tensorflow.Status s = session.Create(def);
        if (!s.ok()) {
            throw new RuntimeException(s.error_message().getString());
        }
    }

}
