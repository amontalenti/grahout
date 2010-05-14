import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.NullRescorer;

def numNeighbors = 1

// load up a file mapping user id => item id
def model = new FileDataModel(new File("model"))
// calculate tanimoto coefficient to figure out similarity / distances
def similarity = new TanimotoCoefficientSimilarity(model)
// create a nearest n neighbors neighborhood -- in this case, I only care about a single neighbor
def neighborhood = new NearestNUserNeighborhood(numNeighbors, similarity, model)
// create the recommender that wires it all together
def recommender = new GenericUserBasedRecommender(model, neighborhood, similarity)

// magic: do the recommendation
def userToRec = 3
def numItemsToRec = 1
println recommender.recommend(userToRec, numItemsToRec, new NullRescorer())
