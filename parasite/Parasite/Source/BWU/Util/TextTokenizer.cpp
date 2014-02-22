#include "BWU/Util/TextTokenizer.h"

#include <sstream>
#include <iterator>

using std::stringstream;
using std::istream_iterator;
using std::string;
using std::vector;

namespace BWU
{
	vector<string> TextTokenizer::tokenize(string text)
	{
		stringstream textStream(text);

		istream_iterator<string> it(textStream);
		istream_iterator<string> end;
		vector<string> results(it, end);

		return results;
	}
}


