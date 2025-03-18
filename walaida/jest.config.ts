const defineJestConfig =
  require("@tarojs/test-utils-react/dist/jest.js").default;

const jestConfig = defineJestConfig({
  testEnvironment: "jsdom",
  testMatch: ["<rootDir>/__tests__/?(*.)+(spec|test).[jt]s?(x)"],
});

module.exports = jestConfig;
