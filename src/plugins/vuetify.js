// Styles
import "@mdi/font/css/materialdesignicons.css";
import "vuetify/styles";

// Vuetify
import { createVuetify } from "vuetify";

import { VDateInput } from "vuetify/labs/VDateInput";

export default createVuetify({
  components: {
    VDateInput,
  },
}); // https://vuetifyjs.com/en/introduction/why-vuetify/#feature-guides
