import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.NullRescorer;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.recommender.FarthestNeighborClusterSimilarity;
import org.apache.mahout.cf.taste.impl.recommender.TreeClusteringRecommender;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.clustering.lda.LDAInference;
import org.apache.mahout.clustering.lda.LDAState;

// load up a file mapping user id => item id
def model = new FileDataModel(new File("model"))

def basicTanimoto = { numNeighbors ->
    // this algorithm is probably the best one to go with for experimentation
    // purposes, as it has the easiest-to-predict results.  Unfortunately,
    // it is difficult to precompute aspects of this algorithm, so it is only
    // really good for batch calculations on an existing data set.
    //
    // calculate tanimoto coefficient to figure out similarity / distances
    def similarity = new TanimotoCoefficientSimilarity(model)
    // create a nearest n neighbors neighborhood -- in this case, I only care about a single neighbor
    def neighborhood = new NearestNUserNeighborhood(numNeighbors, similarity, model)
    // create the recommender that wires it all together
    def recommender = new GenericUserBasedRecommender(model, neighborhood, similarity)
    return recommender
}

def basicCluster = { -> numClusters
    // this algorithm builds up a model of user preferences by first placing
    // them into taste clusters.  The clusterSimilarity can be precomputed and
    // stored in order to speed up recommendations.  In other words, we first
    // cluster the users, and then we can do a recommendation that says
    // something like, "if any users in this cluster like this thing, then
    // we can recommend it to other users in the cluster."
    //
    def similarity = new LogLikelihoodSimilarity(model);
    def clusterSimilarity = new FarthestNeighborClusterSimilarity(similarity);
    return new TreeClusteringRecommender(model, clusterSimilarity, numClusters);
}

def basicSVD = { numFeatures, steps ->
    // this algorithm is still experimental, but uses the matrix factorization
    // approaches to detect "latent" features in the model that other 
    // graph-based approaches can't find.  Unfortunately, it's dog slow.
    //
    return new SVDRecommender(model, numFeatures, steps);
}

def rec = { recommender, userToRec, numItemsToRec ->
    // do the recommendation
    println recommender.recommend(userToRec, numItemsToRec, new NullRescorer())
}

def r = basicTanimoto(10)
// def r = basicCluster(5)
// def r = basicSVD(2, 10)
rec(r, 3, 2)
